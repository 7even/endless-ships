import React from 'react';
import { connect } from 'react-redux';
import { kebabCase } from '../common';

const ShipPage = ({ ship }) => {
  return (
    <div>Ship {ship.name}</div>
  );
};

const mapStateToProps = (state, { match: { params: { shipName } } }) => {
  return {
    ship: state.data.find(ship => kebabCase(ship.name) === shipName)
  };
};

export default connect(mapStateToProps)(ShipPage);
