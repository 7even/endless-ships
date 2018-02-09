import React from 'react';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';

const Navigation = ({ location: { pathname } }) => {
  const items = [
    ['/',        'Ships',   '/ships'],
    ['/outfits', 'Outfits', '/outfits']
  ].map(([url, text, prefix]) => {
    const active = pathname === url || pathname.startsWith(prefix);

    return (
      <li role="presentation" className={active ? 'active' : ''} key={url}>
        <Link to={url}>{text}</Link>
      </li>
    );
  });

  return <ul className="nav nav-tabs">{items}</ul>;
};

export default withRouter(Navigation);
