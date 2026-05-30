
*** Settings ***
Library           Collections
Library           OperatingSystem
Library           BuiltIn
Library           String


*** Variables ***
${VALID_STATUSES}        succeeded    failed    stopped
${VALID_SCHEDULERS}      ImmediateJobScheduler    OneTimeJobScheduler

*** Test Cases ***
Test Job Termination Handling
    [Setup]    Log    Starting test for job termination handling.

    # Example job dictionary. Replace with actual object structure if necessary.
    ${job}    Create Dictionary
    ...    id=1
    ...    status=failed
    ...    endDate=2025-01-01T12:00:00Z
    ...    jobSpecification=Create Dictionary
    ...    JobScheduler=Create Dictionary
    ...    @type=ImmediateJobScheduler

    Handle Job Termination    ${job}
    Log    Test for job ID ${job['id']} completed
*** Keywords ***
Handle Job Termination
    [Arguments]    ${job}
    Log    Processing job with ID: ${job['id']}

    ${status}    Set Variable    ${job['status']}
    ${scheduler_type}    Set Variable    ${job['jobSpecification']['JobScheduler']['@type']}

    Run Keyword If    '${status}' in ${VALID_STATUSES} AND ${scheduler_type} in ${VALID_SCHEDULERS}
    ...    Set JobSpecification Termination Details    ${job}

    Log    Job ${job['id']} processed successfully

Set JobSpecification Termination Details
    [Arguments]    ${job}
    Log    Setting termination details for job with ID: ${job['id']}

    ${end_date}    Set Variable    ${job['endDate']}
    Set To Dictionary    ${job['jobSpecification']}    endDate=${end_date}
    Set To Dictionary    ${job['jobSpecification']}    status=terminated

    Log    JobSpecification updated with endDate: ${end_date} and status: terminated


