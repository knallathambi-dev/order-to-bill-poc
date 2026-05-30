<!--
SPDX-FileCopyrightText: 2025, 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# User Role & Permission Management Documentation

This project build and exposes _User Role & Permission Management_ documentation as [GitLab pages](https://discobole.ow2.io/disco-oda-components/disco-security/doc).

## Community

You can chat with the core team on [![Discord](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## MkDocs plug-ins

| Plugin name | Description |
|-|-|
|[admonitions](https://squidfunk.github.io/mkdocs-material/reference/admonitions/)| Admonitions, also known as call-outs, are an excellent choice for including side content without significantly interrupting the document flow.|
| [Details](https://facelessuser.github.io/pymdown-extensions/extensions/details/) | Details is an extension that creates collapsible elements that hide their content. |
 [Emoji](https://facelessuser.github.io/pymdown-extensions/extensions/emoji/) | The Emoji extension adds support for inserting emoji via simple short names enclosed within colons: :short_name:. |
 [SuperFences](https://facelessuser.github.io/pymdown-extensions/extensions/superfences/) | SuperFence is an extension that enhance code fences (Code Highlighting,Mermaid diagram, Code Block Title Headers, ... ). |
 |[Truly sane lists](https://github.com/radude/mdx_truly_sane_lists)| Truly sane lists is an extension that helps fixing markdown list indentation.|

## Getting Started

The Discobole User Role & Permission management website can be built and tested locally in three modes:

- dynamic: render all User Role & Permission management mkdocs documents stored in this repository and various external documents stored in other User Role & Permission management projects
- homepage: build and run the `index.html` the User Role & Permission management homepage

### Discobole User Role & Permission management website with only static pages

If you only need to work on local User Role & Permission management documents (files inside the `docs` folder ), run the website with only static pages.

1. Install [MkDocs](https://www.mkdocs.org/#installation), [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) and all other requirements

    ```bash
    pip install -r requirements.txt
    ```

2. Run with `mkdocs serve`
3. Open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser

In this mode, MkDocs updates any change on-the-fly.

### Discobole User Role & Permission management website with dynamic pages

If you need to work also with documents from others User Role & Permission management projects, run the website with dynamic pages.

1. Install [MkDocs](https://www.mkdocs.org/#installation), [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) and all other requirements

    ```bash
    pip install -r requirements.txt
    ```

2. Export on your shell those variables for `process-doc.sh` reponsible to crawl locally various files from User Role & Permission management projects

    |Name|Description|Example
    |-|-|-|
    | `GITLAB_TOKEN` | Project Gitlab token with `read_repository` rights||
    | `CI_SERVER_HOST` | GitLab server host | `gitlab.ow2.org` |
    | `BASE_API_URL` | GitLab api url, | `https://gitlab.ow2.org/api/v4` |
    | `User Role_GROUPS` | List of User Role groups to fetch, | `[{"path": "discobole/disco-oda-components/disco-security", "visibility": "internal"},  "exclude": ["security-authorization-tests"]  ]` |

3. Run the fetch script with `./process-doc.sh`
4. Run with `mkdocs serve -f mkdocs-with-toc.yml`
5. Open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser

## How to configure User Role & Permission management documentation generation

Some pages of the site are built by exploring one or more GitLab groups/subgroups/projects, looking for `README.md` files.

It can be configured with the following variables:

- `GITLAB_TOKEN`: a [personal access token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html) with scopes `api,read_repository`
  and at least `Developer` role on all groups & projects to crawl (not required if only `public` groups and projects),
- `GITLAB_GROUPS`: JSON configuration of GitLab groups to crawl.

Here is an example of `GITLAB_GROUPS` content:

```json
[
  {
    "path": "discobole/disco-oda-components/disco-security",
    "visibility": "internal",
     "exclude": ["security-authorization-tests"]
  }
]
```

Some explanations:

- `path` is a path to a [GitLab group](https://docs.gitlab.com/ee/user/group/)
  with [GitLab projects](https://docs.gitlab.com/ee/user/project/) containing project detailes information resources (`README.md`).
- `visibility` is the group/projects visibility to crawl.
- `exclude` (optional) allows to exclude some project(s) from processing.

The `GITLAB_GROUPS` is defined as a variable to crawl the `microservices` User Role's sub-groups only.

## Build With

- [MkDocs](https://www.mkdocs.org/) - Project documentation with Markdown.
- [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) - Material theme for MkDocs

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](../../tags).

## Authors

See the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.