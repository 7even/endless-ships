import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { kebabCase } from '../common';

const ShipsList = ({ ships }) => {
  return (
    <ul>
      {ships.map(ship => (
        <li key={ship.name}>
          <Link to={`/ships/${kebabCase(ship.name)}`}>
            {ship.name}
          </Link>
        </li>
      ))}
    </ul>
  );
};

const mapStateToProps = (state) => {
  return {
    ships: state.data
  };
};

export default connect(mapStateToProps)(ShipsList);
