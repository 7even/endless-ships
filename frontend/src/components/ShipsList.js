import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import R from 'ramda';
import { kebabCase } from '../common';

const licenses = {
  'Navy':               'human',
  'Carrier':            'human',
  'Cruiser':            'human',
  'Militia Carrier':    'human',
  'Unfettered Militia': 'hai',
  'Wanderer':           'wanderer',
  'Wanderer Military':  'wanderer',
  'Coalition':          'coalition',
  'Heliarch':           'coalition',
  'Remnant':            'remnant'
};

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
    ships: state.data,
    races: R.uniq(state.data.map(ship => ship.race)),
    categories: R.uniq(state.data.map(ship => ship.category))
  };
};

export default connect(mapStateToProps)(ShipsList);
