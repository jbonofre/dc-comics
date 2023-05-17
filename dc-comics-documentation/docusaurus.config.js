/*
 * Copyright © 2023 - Dremio - https://www.dremio.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'DC Comics',
  tagline: 'Dremio Cloud service framework',
  favicon: 'img/favicon.ico',

  // Set the production url of your site here
  url: 'https://jbonofre.github.io',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/dc-comics',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'jbonofre', // Usually your GitHub org/user name.
  projectName: 'dc-comics', // Usually your repo name.

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        blog: {
          showReadingTime: true,
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      // Replace with your project's social card
      image: 'img/docusaurus-social-card.jpg',
      navbar: {
        logo: {
          alt: 'DC Comics',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'intro',
            position: 'right',
            label: 'Documentation',
          },
          {
            label: 'GitHub',
            href: 'https://github.com/jbonofre/dc-comics',
            position: 'right',
          },
					{
						type: 'html',
						position: 'right',
						value: '<a href="https://www.quarkus.io"><img src="https://www.vhv.rs/dpng/d/257-2577905_quarkus-logo-png-transparent-png.png" width="40px"/></a>',
					},
					{
						type: 'html',
						position: 'right',
						value: '<a href="https://www.dremio.com"><img src="https://www.dremio.com/wp-content/uploads/2022/03/Dremio-logo.png" width="80px"/></a>',
					}
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            label: 'Issues',
            href: 'https://github.com/jbonofre/dc-comics/issues',
          },
          {
            label: 'Ci/CD',
            href: 'https://github.com/jbonofre/dc-comics/actions',
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Dremio, Inc. Built with Docusaurus.`
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
    }),
};

module.exports = config;
