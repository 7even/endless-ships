import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import VisibleSortedShips from './VisibleSortedShips';
import ShipsFilter from './ShipsFilter';

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

const ShipsList = ({ races, categories, filtersCollapsed }) => {
  return (
    <div className="app">
      <ShipsFilter races={races}
                   categories={categories}
                   licenses={Object.keys(licenses)}
                   filtersCollapsed={filtersCollapsed} />
      <VisibleSortedShips licenses={licenses} />
    </div>
  );
};

const mapStateToProps = (state) => {
  return {
    races: R.uniq(state.data.map(ship => ship.race)),
    categories: R.uniq(state.data.map(ship => ship.category)),
    filtersCollapsed: state.filtersCollapsed
  };
};

export default connect(mapStateToProps)(ShipsList);
