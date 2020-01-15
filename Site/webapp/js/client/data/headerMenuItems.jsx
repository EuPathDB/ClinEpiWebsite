import React from 'react';
import { StudyMenuItem } from 'ebrc-client/App/Studies';
import { menuItemsFromSocials, iconMenuItemsFromSocials } from 'ebrc-client/App/Utils/Utils';
import { getStaticSiteData } from '../selectors/siteData';

export default function headerMenuItems (state) {
  const { siteConfig } = state.globalData;
  const siteData = getStaticSiteData(state);
  const { studies } = siteData;
  const { youtubeUrl } = siteConfig;
  const socialIcons = iconMenuItemsFromSocials(siteConfig);
  const socialLinks = menuItemsFromSocials(siteConfig);

  return {
    mainMenu: [
      {
        id: 'search',
        text: 'Search a Study',
        children:[{
            text: <div style={{ padding: '0.5em 0' }}>All Studies</div>,
            route: '/search/dataset/Studies/result'
          }].concat(  
          studies.entities != null
          ? studies.entities.map(study => ({ text: <StudyMenuItem study={study} config={siteConfig} /> }))
          : [{ text: <i style={{ fontSize: '13em' }} className="fa fa-align-justify"/> }])
      },
      {
        id: 'workspace',
        text: 'Workspace',
        children: [
          {
            text: 'My Search Strategies',
            route: '/workspace/strategies/all'
          },
          {
            text: 'My Basket',
            route: '/workspace/basket',
            loginRequired: true
          },
          {
            text: 'Public Search Strategies',
            route: '/workspace/strategies/public'
          }
        ]
      },
      {
        id: 'community',
        text: 'Community',
        children: [
          {
            text: 'About ClinEpiDB',
            route: '/community/ClinEpiDB/about.html'
          },
          {
            text: 'News',
            route: '/community/ClinEpiDB/news.html'
          },
          { 
            text: 'FAQ',
            route: '/community/ClinEpiDB/faq.html'
          },
          {
            text: 'Public Strategies',
            route: '/workspace/strategies/public'
          },
          {
            text: 'Video Tutorials',
            url: youtubeUrl,
            target: '_blank'
          },
          {
            text: 'Tutorials and Resources',
            route: '/community/ClinEpiDB/resources.html'
          },
          {
            text: 'Data Access & Use Policy',
            url: '/documents/DataAccessUsePolicyClinEpiDB.latest.pdf'
          },
          ...socialLinks
        ]
      },
      {
        target: '_blank',
        id: 'contactus',
        text: 'Contact Us',
        route: '/contact-us'
      }
    ],
    iconMenu: [ ...socialIcons ]
  }
};
