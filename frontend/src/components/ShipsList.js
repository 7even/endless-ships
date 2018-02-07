import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import VisibleSortedShips from './VisibleSortedShips';

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

const ShipsList = () => {
  return <VisibleSortedShips licenses={licenses} />;
};

const mapStateToProps = (state) => {
  return {
    races: R.uniq(state.data.map(ship => ship.race)),
    categories: R.uniq(state.data.map(ship => ship.category))
  };
};

export default connect(mapStateToProps)(ShipsList);
