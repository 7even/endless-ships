import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import './index.css';
import 'github-fork-ribbon-css/gh-fork-ribbon.css';

fetch('ga.json').then(response => response.json()).then(gaSettings => {
  if (gaSettings.gaId) { // eslint-disable-next-line
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){ // eslint-disable-next-line
    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

    window.ga('create', gaSettings.gaId, 'auto');
    window.ga('send', 'pageview');
  } else {
    console.log('Google Analytics disabled');
  }
});

ReactDOM.render(
  <App />,
  document.getElementById('root')
);
