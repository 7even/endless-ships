import React from 'react';

import ShipsFilter from './ShipsFilter';
import VisibleSortedShips from './VisibleSortedShips';

const ShipsList = () => {
  return (
    <div className="app">
      <ol className="breadcrumb">
        <li className="active">Ships</li>
      </ol>

      <ShipsFilter />
      <VisibleSortedShips />
    </div>
  );
};

export default ShipsList;
