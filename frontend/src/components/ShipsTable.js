import React from 'react';

import Table from './Table';
import ShipRow from './ShipRow';

const ShipsTable = ({ ships, headers, ordering, toggleOrdering }) => (
  <Table headerColumns={headers}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {ships.map(ship => <ShipRow ship={ship} key={ship.name} />)}
  </Table>
);

export default ShipsTable;
