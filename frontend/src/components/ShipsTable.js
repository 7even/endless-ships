import React from 'react';
import { Table } from 'react-bootstrap';

import ShipRow from './ShipRow';
import { TableHeaders } from '../ordering';

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

const ShipsTable = ({ ships, ordering, toggleOrdering }) => {
  return (
    <Table striped bordered condensed hover>
      <thead>
        <tr>
          <TableHeaders columns={shipsHeaderColumns}
                        ordering={ordering}
                        toggleOrdering={toggleOrdering} />
        </tr>
      </thead>
      <tbody>
        {ships.map(ship => <ShipRow ship={ship} key={ship.name} />)}
      </tbody>
    </Table>
  );
};

export default ShipsTable;
