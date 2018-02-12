import React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Link } from 'react-router-dom';
import { Nav, NavItem, NavDropdown, MenuItem } from 'react-bootstrap';
import R from 'ramda';

import { kebabCase } from '../common';

const Navigation = ({ shipNames, location: { pathname } }) => {
  const shipItems = shipNames.map(name => {
    const path = `/ships/${kebabCase(name)}`;

    return (
      <MenuItem componentClass={Link}
                href={path}
                to={path}
                key={name}>
        {name}
      </MenuItem>
    );
  });

  const onShipsPage = (pathname === "/") || pathname.startsWith('/ships');

  return (
    <Nav bsStyle="tabs">
      <NavDropdown id="ships-dropdown" active={onShipsPage} title="Ships">
        <MenuItem componentClass={Link} href="/" to="/">All ships</MenuItem>
        <MenuItem divider />
        {shipItems}
      </NavDropdown>

      <NavItem componentClass={Link}
               href="/outfits"
               to="/outfits"
               active={pathname.startsWith('/outfits')}>
        Outfits
      </NavItem>
    </Nav>
  );
};

const mapStateToProps = (state) => {
  return {
    shipNames: R.sortBy(R.identity, state.ships.map(ship => ship.name))
  };
};

export default withRouter(connect(mapStateToProps)(Navigation));
