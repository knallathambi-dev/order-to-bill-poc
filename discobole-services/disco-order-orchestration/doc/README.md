<!--
SPDX-FileCopyrightText: 2025, 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Customer Order Orchestration & Distribution Documentation

This project build and exposes COOD DISCOBOLE documentation as [GitLab pages](https://discobole.ow2.io/disco-oda-components/disco-order-orchestration/doc).

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## Getting Started

The documentation website can be built and tested locally in three modes:

- **static**: render only mkdocs documents stored on this repository in the `docs` folder
- **dynamic**: render all mkdocs documents stored in this repository and various external documents stored in other DISCOBOLE Order Orchestration` projects
- **home page**: build and run the `index.html` aka the documentation home page

### Installing

Clone the project with:
  
```bash
git clone https://gitlab.ow2.org/discobole/disco-oda-components/disco-order-orchestration/doc
```

### Documentation only static pages

If you only need to work on documentation documents, run `MkDocs` with only static pages.

1. Install [MkDocs](https://www.mkdocs.org/#installation), [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) and all other requirements

    ```bash
    $ pip install -r requirements.txt
    ```

2. Run with `mkdocs serve`
3. Open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser

In this mode, MkDocs updates any change on the fly.

### Documentation with dynamic pages

If you need to work also with Mardown documents from others Order Orchestration` projects, run `MkDocs` with dynamic pages.

1. Install [MkDocs](https://www.mkdocs.org/#installation), [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) and all other requirements

    ```bash
    $ pip install -r requirements.txt
    ```

2. Export on your shell those variables for `process-doc.sh` responsible to crawl locally various files from `DISCOBOLE-order-orchestration` projects

    |Name|Description|Value|
    |-|-|-|
    | `GITLAB_TOKEN` | a [personal access token](https://docs.gitlab.com/ee/user/group/settings/group_access_tokens.html) with at least scopes `api`,`read_repository` and `Developer` role| *** |
    | `CI_JOB_TOKEN` | a [personal access token](https://docs.gitlab.com/ee/user/group/settings/group_access_tokens.html) with at least scopes `api`,`read_repository` and `Developer` role| `$GITLAB_TOKEN` |    
    | `CI_SERVER_HOST` | GitLab server host | `gitlab.ow2.org` |
    | `BASE_API_URL` | GitLab api url, | `https://gitlab.ow2.org/api/v4` |
    | `GITLAB_GROUPS` | List of groups/projects to fetch | See [example](#how-to-documentation-generation) |

3. Run the fetch script with `./process-doc.sh`
4. Run with `mkdocs serve -f mkdocs-with-toc.yml`
5. Open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser

### Documentation home page

The home page is only just a simple static page not handle by the MkDocs tools.

To run it locally you can use a simple http server, for instance [live-server](https://github.com/tapio/live-server):

```bash
$ sudo npm install -g live-server
$ live-server static
```

## How to documentation generation

Some documentation pages are built by crawling one or several GitLab groups/subgroups/projects, looking for the following Markdown resources:

- **Markdown ADRs** recording the architecture decisions found in the `architecture` project are used to build the architecture decisions pages
- **Markdown READMEs** found in the crawled project are used to build the detailed architecture pages
- **Markdown API specifications** found in the crawled `microservice` project are used to build the API References

It can be configured with the following variables:

- `GITLAB_TOKEN`: a [personal access token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html) with scopes `api,read_repository`
  and at least `Developer` role on all groups & projects to crawl (not required if only `public` groups and projects),
- `CI_JOB_TOKEN`: equal to `GITLAB_TOKEN`  
- `GITLAB_GROUPS`: JSON configuration of GitLab groups to crawl.

By default, `GITLAB_GROUPS` is configured to crawl these projects:

```json
[
    {   
      "path": "discobole/disco-oda-components/disco-order-orchestration",
      "visibility": "internal", 
      "exclude": [
        "orchestration-delivery-commons", 
        "orchestration-delivery-tests",
        "doc"
      ],
      "type": "oda-component",
      "name": "Customer Order Orchestration and Delivery",
      "acronym": "COOD"
    }
]
```

Some explanations:

- `path` is a path to a [GitLab group](https://docs.gitlab.com/ee/user/group/)
  with [GitLab projects](https://docs.gitlab.com/ee/user/project/) containing project detailed information resources (`README.md`).
- `visibility` is the group/projects visibility to crawl.
- `exclude` (optional) allows to exclude some project(s) from processing.

Also, `GITLAB_GROUPS` can be defined as a CI variable in the project's settings.

## Environments

All deployed environments can be found in [GitLab Environments page](../../-/environments).

## Build With

- [MkDocs](https://www.mkdocs.org/) - Project documentation with Markdown.
- [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) - Material theme for MkDocs

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See also the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.md) file for details.

This project is under MIT license.
