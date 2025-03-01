import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

// This runs in Node.js - Don't use client-side code here (browser APIs, JSX...)

const globalVariables = {
  'current': {
    'ebms.core.version': '2.20.2',
    'ebms.branch.version': '2.20.x'
  },
  '2.19.x': {
    'ebms.core.version': '2.19.4',
    'ebms.branch.version': '2.19.x'
  }
}
const config: Config = {
  title: 'EbMS Adapter',
  tagline: 'EbMS 2.0 Specification',
  favicon: 'img/favicon.ico',

  // Set the production url of your site here
  url: 'https://eluinstra.github.io',
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: '/ebms-admin/',
  deploymentBranch: 'gh-pages',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'eluinstra', // Usually your GitHub org/user name.
  projectName: 'ebms-admin', // Usually your repo name.
  trailingSlash: false,

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      {
        docs: {
          includeCurrentVersion: false,
          // lastVersion: 'current',
          // versions: {
          //   current: {
          //     label: '2.20.x',
          //     path: '2.20.x',
          //   },
          // },    
          sidebarPath: './sidebars.ts',
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl:
            'https://github.com/eluinstra/ebms-admin/tree/documentation',
        },
        theme: {
          customCss: './src/css/custom.css',
        },
      } satisfies Preset.Options,
    ],
  ],
  markdown: {
    preprocessor: ({filePath, fileContent}) => {
      var key = '';
      var found = false;
      for (key in globalVariables) {
        let folderName = (key == 'current' ? 'current' : `version-${key}`);
        if (filePath.includes(`/${folderName}/`)) {
          found = true;
          break;
        }
      }
      if (key == '' || !found) {
        key = 'current';
      }
      let content = fileContent;
      for (const variable in globalVariables[key]) {
        content = content.replaceAll('@'+variable+'@', globalVariables[key][variable]);
      }
      return content
    },
  },
  themeConfig: {
    // Replace with your project's social card
    image: 'img/docusaurus-social-card.jpg',
    navbar: {
      title: 'EbMS Adapter',
      logo: {
        alt: 'My Site Logo',
        src: 'img/logo.svg',
      },
      items: [
        {
          type: 'docsVersionDropdown',
        },
        {
          type: 'docSidebar',
          sidebarId: 'tutorialSidebar',
          position: 'left',
          label: 'Documentation',
        },
        {
          href: 'https://github.com/eluinstra/ebms-admin/tree/documentation',
          label: 'GitHub',
          position: 'right',
        },
      ],
    },
    footer: {
      style: 'dark',
      links: [
        {
          title: 'Docs',
          items: [
            {
              label: 'Documentation',
              to: '/docs/intro',
            },
          ],
        },
        {
          title: 'Community',
          items: [
            {
              label: 'Stack Overflow',
              href: 'https://stackoverflow.com/questions/tagged/docusaurus',
            },
            {
              label: 'Discord',
              href: 'https://discordapp.com/invite/docusaurus',
            },
            {
              label: 'X',
              href: 'https://x.com/docusaurus',
            },
          ],
        },
        {
          title: 'More',
          items: [
            {
              label: 'GitHub',
              href: 'https://github.com/facebook/docusaurus',
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} EbMS Adapter, Inc. Built with Docusaurus.`,
    },
    prism: {
      theme: prismThemes.github,
      darkTheme: prismThemes.dracula,
    },
  } satisfies Preset.ThemeConfig,
  plugins: [[require.resolve("docusaurus-lunr-search"), {
    enableHighlight: true
  }]],
};

export default config;
