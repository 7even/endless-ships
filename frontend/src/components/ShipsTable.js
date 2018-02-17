import React from 'react';

import Table from './Table';
import ShipRow from './ShipRow';

const shipsHeaderColumns = [
  ['Name', 'name'],
  ['Race'],
  ['Cost', 'cost'],
  ['Category'],
  ['Hull', 'hull'],
  ['Shields', 'shields'],
  ['Mass', 'mass'],
  ['Engine cap.', 'engineCapacity'],
  ['Weapon cap.', 'weaponCapacity'],
  ['Fuel cap.', 'fuelCapacity'],
  ['Outfit sp.', 'outfitSpace'],
  ['Cargo sp.', 'cargoSpace'],
  ['Crew / bunks', 'bunks'],
  ['Licenses']
];

const ShipsTable = ({ ships, ordering, toggleOrdering }) => (
  <Table headerColumns={shipsHeaderColumns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {ships.map(ship => <ShipRow ship={ship} key={ship.name} />)}
  </Table>
);

export default ShipsTable;
