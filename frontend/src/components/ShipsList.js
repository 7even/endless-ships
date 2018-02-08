import React from 'react';

import ShipsFilter from './ShipsFilter';
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
  return (
    <div className="app">
      <ShipsFilter />
      <VisibleSortedShips licenses={licenses} />
    </div>
  );
};

export default ShipsList;
