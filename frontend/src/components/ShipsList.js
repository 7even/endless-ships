import React from 'react';

import ShipsFilter from './ShipsFilter';
import VisibleSortedShips from './VisibleSortedShips';

const ShipsList = () => {
  return (
    <div className="app">
      <ShipsFilter />
      <VisibleSortedShips />
    </div>
  );
};

export default ShipsList;
