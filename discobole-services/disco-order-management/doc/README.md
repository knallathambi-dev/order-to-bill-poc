<!--
SPDX-FileCopyrightText: 2025, 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Order Management Documentation

This project build and exposes the _Order Management_ *documentation* as [GitLab pages](https://discobole.org.io/disco-oda-components/disco-order-management/doc/).

## Community

You can chat with the core team on [![](https://dcbadge.limes.pink/api/server/pzcwfgqns7?style=flat)](https://discord.gg/pzcwfgqns7).

## MkDocs plug-ins

| Plugin name | Description |
|-|-|
|[admonitions](https://squidfunk.github.io/mkdocs-material/reference/admonitions/)| Admonitions, also known as call-outs, are an excellent choice for including side content without significantly interrupting the document flow.|
| [Details](https://facelessuser.github.io/pymdown-extensions/extensions/details/) | Details is an extension that creates collapsible elements that hide their content. |
 [Emoji](https://facelessuser.github.io/pymdown-extensions/extensions/emoji/) | The Emoji extension adds support for inserting emoji via simple short names enclosed within colons: :short_name:. |
 [SuperFences](https://facelessuser.github.io/pymdown-extensions/extensions/superfences/) | SuperFence is an extension that enhance code fences (Code Highlighting,Mermaid diagram, Code Block Title Headers, ... ). |
 |[Truly sane lists](https://github.com/radude/mdx_truly_sane_lists)| Truly sane lists is an extension that helps fixing markdown list indentation.|

## Getting Started

The _Order Management documentation_ can be built and tested locally in three modes:

- dynamic: render all Order Management mkdocs documents stored in this repository and various external documents stored in other projects
- homepage: build and run the `index.html` the Lizard homepage

### Order Management documentation with only static pages

If you only need to work on local Order Management documents (files inside the `docs` folder ), run the documentation with only static pages.

1. Install [MkDocs](https://www.mkdocs.org/#installation), [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) and all other requirements

    ```bash
    pip install -r requirements.txt
    ```

2. Run with `mkdocs serve`
3. Open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser

In this mode, MkDocs updates any change on-the-fly.

### Order Management documentation with dynamic pages

If you need to work also with documents from others DISCOBOLE projects, run the documentation with dynamic pages.

1. Install [MkDocs](https://www.mkdocs.org/#installation), [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) and all other requirements

    ```bash
    pip install -r requirements.txt
    ```

2. Export on your shell those variables for `process-doc.sh` responsible to crawl locally various files from DISCOBOLE projects

    | Name             | Description                                        | Example                                                      |
    | ---------------- | -------------------------------------------------- | ------------------------------------------------------------ |
    | `GITLAB_TOKEN`   | Project Gitlab token with `read_repository` rights |                                                              |
    | `CI_SERVER_HOST` | GitLab server host                                 | `gitlab.ow2.org`                                             |
    | `BASE_API_URL`   | GitLab api url,                                    | `https://gitlab.ow2.org/api/v4`                              |
    | `GITLAB_GROUPS`  | List of Order management groups to fetch,          | `[{"path": "discobole/disco-oda-components/disco-order-management", "visibility": "internal","exclude": ["order-commons","order-tests","order-docker-platform"]}]` |

3. Run the fetch script with `./process-doc.sh`
4. Run with `mkdocs serve -f mkdocs-with-toc.yml`
5. Open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser

## How to configure the documentation generation

Some pages of the site are built by exploring one or more GitLab groups/subgroups/projects, looking for `README.md` files.

It can be configured with the following variables:

- `GITLAB_TOKEN`: a [personal access token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html) with scopes `api,read_repository`
  and at least `Developer` role on all groups & projects to crawl (not required if only `public` groups and projects),
- `GITLAB_GROUPS`: JSON configuration of GitLab groups to crawl.

Here is an example of `GITLAB_GROUPS` content:

```json
[
  {
    "path": "discobole/disco-oda-components/disco-order-management",
    "visibility": "internal",
    "exclude": [
      "order-commons",
      "order-tests",
      "order-docker-platform"
    ]
  }
]
```

Some explanations:

- `path` is a path to a [GitLab group](https://docs.gitlab.com/ee/user/group/)
  with [GitLab projects](https://docs.gitlab.com/ee/user/project/) containing project detailes information resources (`README.md`).
- `visibility` is the group/projects visibility to crawl.
- `exclude` (optional) allows to exclude some project(s) from processing.

The `GITLAB_GROUPS` is defined as group variable in group `discobole` is configured to crawl the `microservices` DISCOBOLE's sub-groups only.

## Tracking script configuration

Another thing that can be configured is how you will track audience on the _Order Management documentation_.

All you have to do is to define a `TRACKING_JS` variable with tracking JavaScript code ([Google Analytics](https://analytics.google.com/), [Matomo](https://matomo.org/) or else).

Example of `TRACKING_JS` value for Google Analytics:

```javascript
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-123456789-1', 'auto');
ga('send', 'pageview');
```

## Build With

- [MkDocs](https://www.mkdocs.org/) - Project documentation with Markdown.
- [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) - Material theme for MkDocs

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our process for submitting merge requests to us.

## Authors

See also the list of [contributors](CONTRIBUTORS.md) who participated in this project.

## License

This project is licensed - see the [LICENSE](LICENSE.txt) file for details.
