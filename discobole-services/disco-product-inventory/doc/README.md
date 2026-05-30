<!--
SPDX-FileCopyrightText: 2025, 2026 Orange SA
SPDX-License-Identifier: MIT

This software is distributed under the MIT License,
the text of which is available at https://opensource.org/license/mit
or see the "LICENSE.txt" file for more details.

Authors: See CONTRIBUTORS.txt
-->

# Disco Product Inventory website

This project build and exposes _Disco Product Inventory_ website as [GitLab pages](https://discobole.ow2.io//disco-oda-components/disco-product-inventory/product-inventory-website/).

<!-- ## Community

You can mail [Product Inventory mail list](mailto:disco.cpib@sofrecom.com) channel on teams. -->

## MkDocs plug-ins

| Plugin name | Description |
|-|-|
|[admonitions](https://squidfunk.github.io/mkdocs-material/reference/admonitions/)| Admonitions, also known as call-outs, are an excellent choice for including side content without significantly interrupting the document flow.|
| [Details](https://facelessuser.github.io/pymdown-extensions/extensions/details/) | Details is an extension that creates collapsible elements that hide their content. |
 [Emoji](https://facelessuser.github.io/pymdown-extensions/extensions/emoji/) | The Emoji extension adds support for inserting emoji via simple short names enclosed within colons: :short_name:. |
 [SuperFences](https://facelessuser.github.io/pymdown-extensions/extensions/superfences/) | SuperFence is an extension that enhance code fences (Code Highlighting,Mermaid diagram, Code Block Title Headers, ... ). |
 |[Truly sane lists](https://github.com/radude/mdx_truly_sane_lists)| Truly sane lists is an extension that helps fixing markdown list indentation.|

## Getting Started

The Product Inventory website can be built and tested locally in three modes:

- dynamic: render all Product Inventory mkdocs documents stored in this repository and various external documents stored in other Product Inventory projects
- homepage: build and run the `index.html` the Product Inventory homepage

### Product Inventory website with only static pages

If you only need to work on local Product Inventory documents (files inside the `docs` folder ), run the website with only static pages.

1. Install [MkDocs](https://www.mkdocs.org/#installation), [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) and all other requirements

    ```bash
    pip install -r requirements.txt
    ```

2. Run with `mkdocs serve`
3. Open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser

In this mode, MkDocs updates any change on-the-fly.

### Product Inventory website with dynamic pages

If you need to work also with documents from others Product Inventory projects, run the website with dynamic pages.

1. Install [MkDocs](https://www.mkdocs.org/#installation), [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) and all other requirements

    ```bash
    pip install -r requirements.txt
    ```

2. Export on your shell those variables for `process-doc.sh` reponsible to crawl locally various files from Product Inventory projects

    |Name|Description|Example
    |-|-|-|
    | `GITLAB_TOKEN` | Project Gitlab token with `read_repository` rights||
    | `CI_SERVER_HOST` | GitLab server host | `gitlab.ow2.org` |
    | `BASE_API_URL` | GitLab api url, | `https://gitlab.ow2.org/api/v4` |
    | `GITLAB_GROUPS` | List of Product Inventory groups to fetch, | `[{"path": "discobole/disco-oda-components/disco-product-inventory", "visibility": "internal"}]` |

3. Run the fetch script with `./process-doc.sh`
4. Run with `mkdocs serve -f mkdocs-with-toc.yml`
5. Open [http://127.0.0.1:8000](http://127.0.0.1:8000) in your browser

### Installing

Follow these steps to install the dependencies and set up the environment for the Product Inventory website.

### Prerequisites

Before you begin, ensure that the following software is installed:

- **Python 3.9** (or later)
- **MkDocs** (for building the documentation)
- **Material for MkDocs** (theme for MkDocs)

### Step-by-Step Installation

1. **Install Python 3.9+**

   Download and install Python 3.9 or later from the [official Python website](https://www.python.org/downloads/).

2. **Clone the Repository**

   Clone the repository to your local machine by running the following command:

  ```bash
   git clone https://gitlab.ow2.org/discobole/disco-oda-components/disco-product-inventory/product-inventory-website.git
   cd product-inventory-website
   ```

3. **Create virtual env if no**
   ```bash
    python -m venv venv
   ```
   
4. **Use the virtual env**
   ```bash
    source venv/bin/active
   ```
   
5. **Install the dependencies**
   ```bash
    pip install requirements.txt
   ```
## Environments

The Product Inventory website can be deployed in different environments based on the setup and requirements. Below are the common environments you may need to work with:

### Development Environment

This is the default environment used for local development. It allows you to run the site using static pages only, and changes are reflected immediately after saving.

- **Server**: Local server (`http://127.0.0.1:8000`)
- **Mode**: Static pages
- **Setup**: No external repositories are crawled, only local documents in the `docs` folder.

### Staging Environment

The staging environment is used for pre-production testing. It allows you to build and preview the Product Inventory website with dynamic content pulled from external sources like GitLab repositories.

- **Server**: Staging server URL
- **Mode**: Dynamic pages (fetches external documentation)
- **Setup**: Requires configuration of `GITLAB_TOKEN` and `GITLAB_GROUPS`.

### Production Environment

This is the final deployment environment for the website.

- **Server**: Public-facing production server
- **Mode**: Dynamic pages with additional optimizations for performance and security.
- **Setup**: Similar to the staging environment but with production-specific configurations.

## How to configure Product Inventory website generation

Some pages of the site are built by exploring one or more GitLab groups/subgroups/projects, looking for `README.md` files.

It can be configured with the following variables:

- `GITLAB_TOKEN`: a [personal access token](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html) with scopes `api,read_repository`
  and at least `Developer` role on all groups & projects to crawl (not required if only `public` groups and projects),
- `GITLAB_GROUPS`: JSON configuration of GitLab groups to crawl.

Here is an example of `GITLAB_GROUPS` content:

```json
[
   {
      "path":"disco/disco-oda-components/disco-product-inventory",
      "visibility":"internal"
   }
]
```

Some explanations:

- `path` is a path to a [GitLab group](https://docs.gitlab.com/ee/user/group/)
  with [GitLab projects](https://docs.gitlab.com/ee/user/project/) containing project detailes information resources (`README.md`).
- `visibility` is the group/projects visibility to crawl.
- `exclude` (optional) allows to exclude some project(s) from processing.

The `GITLAB_GROUPS` is defined as group variable in group `disco-product-inventory` is configured to crawl the `microservices` Product Inventory's sub-groups only.

## Tracking script configuration

Another thing that can be configured is how you will track audience on the _Product Inventory_ website.

All you have to do is to defined a `TRACKING_JS` variable with tracking JavaScript code ([Google Analytics](https://analytics.google.com/), [Matomo](https://matomo.org/) or else).

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

This project is licensed - see the [LICENSE](LICENSE.md) file for details.

## Versioning

We follow [Semantic Versioning](https://semver.org/) for this project. Version numbers are assigned according to the following format:

## Acknowledgments

- Thanks to [**The people behind Product Inventory**](https://discobole.ow2.io/disco-oda-components/disco-product-inventory/doc/about/people/#the-people-behind-product-inventory)

