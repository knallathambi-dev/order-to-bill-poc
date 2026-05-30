# SPDX-FileCopyrightText: 2025 Orange SA
# SPDX-License-Identifier: MIT
#
# This software is distributed under the MIT License,
# the text of which is available at https://opensource.org/license/mit
# or see the "LICENSE.txt" file for more details.
#
# Authors: See CONTRIBUTORS.txt

import os
import requests
import psutil
from dateutil import parser
import xml.etree.ElementTree as ET
#from bs4 import BeautifulSoup
import urllib3
import re
#from lxml import html
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)



os.makedirs("output", exist_ok=True)

GITLAB_TOKEN = os.environ.get("PRIVATE_API_TOKEN")
#GITLAB_TOKEN = "glpat-sygKKysx3KeY7xxZSsvN"
print("PRIVATE_API_TOKEN =", GITLAB_TOKEN)
PROJECT_ID = os.environ['CI_PROJECT_ID']
PIPELINE_ID = os.environ['CI_PIPELINE_ID']
PROJECT_NAME = os.environ['CI_PROJECT_NAME']
HEADERS = {"PRIVATE-TOKEN": GITLAB_TOKEN}

def format_duration(seconds):
    mins, secs = divmod(int(seconds), 60)
    hrs, mins = divmod(mins, 60)
    return f"{hrs:02}:{mins:02}:{secs:02}"

def get_pipeline_schedules(gitlab_url, headers, project_id):
    """
    Récupère les schedules planifiés pour un projet GitLab
    et vérifie s'ils sont programmés entre 3h et 6h du matin.
    """
    url = f"{gitlab_url}/api/v4/projects/{project_id}/pipeline_schedules"
    response = requests.get(url, headers=headers)
    schedules_output = []

    if response.status_code == 200:
        schedules = response.json()
        if not schedules:
            schedules_output.append("Aucun schedule trouvé pour ce projet.")
        else:
            for sch in schedules:
                cron = sch.get('cron', '')
                cron_parts = cron.split()
                hour = None
                if len(cron_parts) >= 2:
                    try:
                        hour = int(cron_parts[1])
                    except ValueError:
                        hour = None

                status = (hour is not None and 3 <= hour <= 6)

                schedules_output.append({
                    "id": sch.get('id'),
                    "description": sch.get('description'),
                    "cron": cron,
                    "hour": hour,
                    "next_run_at": sch.get('next_run_at'),
                    "active": sch.get('active'),
                    "is_hour_ok": status
                })
    else:
        schedules_output.append(f"Erreur lors de la récupération des schedules : {response.status_code} {response.text}")
    return schedules_output

# --- Vérification des schedules ---
print("\n Vérification des horaires des pipeline schedules...\n")
schedules = get_pipeline_schedules("https://gitlab.tech.orange", HEADERS, PROJECT_ID)
for sch in schedules:
    if isinstance(sch, str):
        print(sch)
    else:
        statut = "Horaire OK (entre 3h et 6h)" if sch["is_hour_ok"] else " Horaire NON conforme (hors 3h-6h)"
        print(f"- Schedule ID: {sch['id']}")
        print(f"  Description: {sch['description']}")
        print(f"  Cron: {sch['cron']} (heure programmée: {sch['hour'] if sch['hour'] is not None else 'inconnue'}h)")
        print(f"  Prochain run: {sch['next_run_at']}")
        print(f"  Actif: {sch['active']}")
        print(f"  Vérification horaire: {statut}\n")




result = []
failed_jobs = []
skipped_jobs = []
successful_jobs = []
canceled_jobs = []

total_tests_global = 0
passed_tests_global = 0
failed_tests_global = 0
skipped_tests_global = 0
total_tests = 0
total_size_kb = 0
passed = 0
failed = 0
skipped = 0
THRESHOLD_CO2_G = 200.0  # en grammes de CO₂
THRESHOLD_ENERGY_WH = 200.0    # 200 Wh par run
THRESHOLD_failure_rate = 30
test_type = os.getenv("TEST_TYPE")
print(test_type)

if test_type =="cypress":
    Type="backend"
elif test_type =="robot":
    Type="frontend"

jobs_url = f"https://gitlab.com/api/v4/projects/{PROJECT_ID}/pipelines/{PIPELINE_ID}/jobs"
#jobs_url = f"https://gitlab.tech.orange/api/v4/projects/{PROJECT_ID}/pipelines/{PIPELINE_ID}/jobs"
print("PROJECT_ID =", PROJECT_ID)
print("PIPELINE_ID =", PIPELINE_ID)
print("PROJECT_NAME =", PROJECT_NAME)
print("URL utilisée :", jobs_url)
jobs_response = requests.get(jobs_url, headers=HEADERS)
print(jobs_response.text)

if jobs_response.status_code == 200:
    jobs = jobs_response.json()
    total_jobs = len(jobs)


    start_times = []
    end_times = []

    result.append("Durée par job :")
    for job in jobs:
        job_name = job["name"].split(":")[0]
        job_status = job["status"]
        job_id = job["id"]

        started_at = parser.parse(job["started_at"]) if job["started_at"] else None
        finished_at = parser.parse(job["finished_at"]) if job["finished_at"] else None

        if started_at and finished_at:
            start_times.append(started_at)
            end_times.append(finished_at)
            job_duration = (finished_at - started_at).total_seconds()
            result.append(f"\n - {job_name}: {format_duration(job_duration)} ({int(job_duration)} sec)")
        else:
            result.append(f"- {job_name}: durée inconnue")

        if job_status == "success":
            successful_jobs.append(job_name)
        elif job_status == "failed":
            failed_jobs.append(f"- {job_name} : failed")
        elif job_status == "skipped":
            skipped_jobs.append(f"- {job_name} : skipped")
        elif job_status == "canceled":
            canceled_jobs.append(f"- {job_name} : canceled")


        if test_type == "robot":
            xml_filename = f"Output_{job_name}.xml"
            xml_path = f"result/{xml_filename}"
            if os.path.exists(xml_path):
                try:
                    tree = ET.parse(xml_path)
                    root = tree.getroot()

                    stats = root.find("statistics/total/stat")
                    passed = int(stats.get("pass"))
                    failed = int(stats.get("fail"))
                    skipped = int(stats.get("skip"))

                    total_tests = passed + failed + skipped

                    total_tests_global += total_tests
                    passed_tests_global += passed
                    failed_tests_global += failed
                    skipped_tests_global += skipped

                    result.append("--Résultats des tests Robot--")
                    result.append(f"Total des tests : {total_tests}")
                    result.append(f"Tests réussis : {passed}")
                    result.append(f"Tests échoués : {failed}")
                    result.append(f"Tests ignorés : {skipped}")
                    result.append("----------------------------")

                    size_bytes = os.path.getsize(xml_path)
                    size_kb = size_bytes / 1024
                    result.append(f"Poids du fichier {xml_filename} : {size_kb:.2f} KB")
                    total_size_kb+= size_kb
                    result.append(f"\nPoids total de tous les fichiers XML : {total_size_kb:.2f} KB")


                except Exception as e:
                    result.append(f"Erreur lors de la lecture du fichier XML pour le job {job_name} : {e}")
                    result.append("----------------------------")
            else:
                result.append(f"Fichier XML de test non trouvé pour le job {job_name} : {xml_path}")
                result.append("----------------------------")

        elif test_type == "cypress":
            artifact_url = f"https://gitlab.tech.orange/api/v4/projects/{PROJECT_ID}/jobs/{job_id}/artifacts/cypress/reports/mochawesome/index.html"
            print("URL générée :", artifact_url)
            try:
                resp = requests.get(artifact_url, headers=HEADERS, verify=False)
                print("URL Cypress:", artifact_url)
                print("Statut de la réponse:", resp.status_code)

                if resp.status_code == 200:
                    tree = html.fromstring(resp.content)


                    def extract_cypress_test_counts(buttons):
                        passed = failed = -1

                        for btn in buttons:
                            texts = list(btn.itertext())
                            if len(texts) < 2:
                                continue  # icône + nombre attendus

                            icon = texts[0].strip()
                            number_str = texts[1].strip()

                            if not number_str.isdigit():
                                continue

                            number = int(number_str)

                            if icon == "✔":  # Passed (✔)
                                passed = number
                            elif icon == "✖":  # Failed (✖)
                                failed = number

                        return passed, failed

                    #Extraction des boutons
                    buttons = tree.xpath("//button")
                    passed, failed = extract_cypress_test_counts(buttons)

                    #Sécurité si extraction échoue
                    if passed == -1:
                        print("⚠️ Résultat 'Passed' introuvable.")
                        passed = 0
                    if failed == -1:
                        print("⚠️ Résultat 'Failed' introuvable.")
                        failed = 0

                    total_tests = passed + failed

                    #Ajout aux totaux globaux
                    total_tests_global += total_tests
                    passed_tests_global += passed
                    failed_tests_global += failed


                    result.append("-- Résultats des tests Cypress --")
                    result.append(f"Total des tests : {total_tests}")
                    result.append(f"Tests réussis : {passed}")
                    result.append(f"Tests échoués : {failed}")
                    result.append("----------------------------")

                else:
                    result.append(f"Lien Cypress inaccessible (status code {resp.status_code})")
                    result.append("----------------------------")

            except Exception as e:
                result.append(f"Erreur lors de la lecture du fichier HTML Cypress pour le job {job_name} : {e}")
                result.append("----------------------------")


            #try:
            #    resp = requests.get(artifact_url, headers=HEADERS, verify=False)  # verify=False pour éviter l’erreur SSL self-signed
            #    if resp.status_code == 200:
            #        soup = BeautifulSoup(resp.text, "html.parser")
            #        stats_div = soup.find("div", class_="stats")
            #        if stats_div:
            #            passes = stats_div.find("li", class_="passes")
            #            failures = stats_div.find("li", class_="failures")
            #
            #            passed = int("".join(filter(str.isdigit, passes.get_text(strip=True)))) if passes else 0
            #            failed = int("".join(filter(str.isdigit, failures.get_text(strip=True)))) if failures else 0
            #
            #            total_tests = passed + failed
            #
            #            total_tests_global += total_tests
            #            passed_tests_global += passed
            #            failed_tests_global += failed
            #            skipped_tests_global += skipped
            #
            #            result.append("--Résultats des tests Cypress--")
            #            result.append(f"Total des tests : {total_tests}")
            #            result.append(f"Tests réussis : {passed}")
            #            result.append(f"Tests échoués : {failed}")
            #            result.append(f"Tests ignorés : {skipped}")
            #            result.append("----------------------------")
            #        else:
            #            result.append("⚠️ Impossible de trouver le bloc des statistiques dans le rapport HTML Cypress.")
            #            result.append("----------------------------")

            #except Exception as e:
            #    result.append(f"Erreur lors de la lecture du fichier HTML Cypress pour le job {job_name} : {e}")
            #    result.append("----------------------------")
        #else:
        #    result.append(f"Type de test inconnu pour le job {job_name} (TEST_TYPE = {test_type})")
        #    result.append("----------------------------")



        job_data = {
            "entry.1352459158": PIPELINE_ID,
            "entry.47445578": job_name,
            "entry.481980430": test_type,
            "entry.865206529": job_status,
            "entry.552234272": int(job_duration) if started_at and finished_at else -1,
            "entry.1374987310": total_tests,
            "entry.2055566088": passed,
            "entry.1385888558": failed,
            "entry.76126323": skipped
        }

        form_url_job = "https://docs.google.com/forms/d/e/1FAIpQLSeVo8UXuWsUz8ZJC8DG8xtXrpLQQ2kzeCAkteWsFNsZdx7CEQ/formResponse";
        requests.post(form_url_job, data=job_data)

    ESTIMATION_JOB_SECONDS = 37.18
    if start_times and end_times:
        pipeline_started = min(start_times)
        pipeline_finished = max(end_times)
        duration_seconds = (pipeline_finished - pipeline_started).total_seconds()



        duration_seconds += ESTIMATION_JOB_SECONDS


    else:
        duration_seconds = 0
        result.append("Impossible de déterminer la durée totale du pipeline.")


    #power_watts = 50
    cpu_cores = psutil.cpu_count(logical=True)
    power_cpu_watts = cpu_cores * 10  # 10 W par core logique (estimation)

    # RAM
    mem = psutil.virtual_memory()
    ram_gb_used = mem.used / (1024**3)  # mémoire utilisée en Go
    power_ram_watts = ram_gb_used * 2   # estimation : 2 W par Go utilisé (approx.)

    # Puissance totale estimée (CPU + RAM)
    power_watts = power_cpu_watts + power_ram_watts

    energy_wh = (power_watts * duration_seconds)/ 3600
    co2_g = energy_wh * 0.475

    # Comparaison
    if co2_g > THRESHOLD_CO2_G:
        alerte = "⚠️ Alerte CO2"
    else:
        alerte = "OK"

    if energy_wh > THRESHOLD_ENERGY_WH:
        alerte_energy = "⚠️ Alerte cons.energy"
    else:
        alerte_energy ="OK"




    result.insert(0, f"----------------------------")
    result.insert(0, f"CO2 estimé : {co2_g:.2f} gCO2e")
    result.insert(0, f"----------------------------")
    result.insert(0, f"Énergie estimée : {energy_wh:.2f} Wh")
    result.insert(0, f"----------------------------")
    result.insert(0, f"(+{ESTIMATION_JOB_SECONDS} sec estimés pour le job 'estimate_energy')")
    result.insert(0, f"Durée totale du pipeline : {format_duration(duration_seconds)} ({int(duration_seconds)} sec)")


    result.append("\n----------------------------")
    result.append("Résumé global des tests Robot Framework :")
    result.append(f"Total des tests : {total_tests_global}")
    result.append(f"Tests réussis : {passed_tests_global}")
    result.append(f"Tests échoués : {failed_tests_global}")
    result.append(f"Tests ignorés : {skipped_tests_global}")

    taux_echec_global = None
    if total_tests_global > 0:
        taux_echec_global = (failed_tests_global / total_tests_global) * 100
        result.append(f"Taux d’échec global : {taux_echec_global:.2f}%")
        result.append("\n----------------------------")
    else:
        taux_echec_global = 0.0
        result.append("Aucun test trouvé pour calculer un taux d’échec global.")
        result.append("\n----------------------------")

    if taux_echec_global > THRESHOLD_failure_rate:
        alerte_echec = "⚠️ Alerte Taux d'echec"
    else:
        alerte_echec ="OK"

    result.append("\n Jobs réussis :")
    result += [f"- {job_name}" for job_name in successful_jobs]

    result.append("\n Jobs échoués :")
    result += failed_jobs

    result.append("\n Jobs ignorés :")
    result += skipped_jobs

    result.append("\n Jobs annulés :")
    result += canceled_jobs

    if total_jobs > 0:
        taux_echec = (len(failed_jobs) / total_jobs) * 100
    else:
        taux_echec = 0
        result.append("Aucun job trouvé, taux d’échec non applicable.")


    result.append(f"\n Nombre total de jobs exécutés : {total_jobs}")
    result.append(f"Taux d'échec : {taux_echec:.2f}%")



    data = {
        "entry.1908975436": PROJECT_ID,
        "entry.753196198": PIPELINE_ID,
        "entry.251183680": int(duration_seconds),
        "entry.455524682": round(energy_wh, 2),
        "entry.2039769601": round(co2_g, 2),
        "entry.386024194": taux_echec_global,
        "entry.1279415693": alerte,
        "entry.1468300943": alerte_energy,
        "entry.1235568870": Type,
        "entry.2092342781": PROJECT_NAME,
        "entry.988916564": alerte_echec,
        "entry.1805645767": total_size_kb
    }

    # Lien vers formResponse
    form_url = "https://docs.google.com/forms/d/e/1FAIpQLSeTkhzadBUnTsWHt-AXozdyTYwfbSkCGahN2RV2LEujAcXJEA/formResponse";

    # Envoi
    response = requests.post(form_url, data=data)



else:
    result.append(f"Erreur récupération des jobs: {jobs_response.status_code} {jobs_response.text}")


with open("output/result.txt", "w") as f:
    f.write("\n".join(result))


print("\n".join(result))
