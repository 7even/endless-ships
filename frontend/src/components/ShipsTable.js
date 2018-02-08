import React from 'react';
import { Table } from 'react-bootstrap';

import ShipRow from './ShipRow';

const ShipsTableHeaders = ({ ordering, toggleOrdering }) => {
  const columns = [
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

  return columns.map(([text, sortBy]) => {
    let title, icon;

    if (sortBy) {
      title = <a className="table-header" onClick={() => toggleOrdering(sortBy)}>{text}</a>;

      if (ordering.columnName === sortBy) {
        if (ordering.order === 'asc') {
          icon = <span className="glyphicon glyphicon-sort-by-attributes"></span>;
        } else {
          icon = <span className="glyphicon glyphicon-sort-by-attributes-alt"></span>;
        }
      }
    } else {
      title = text;
    }

    return (
      <th className="text-center" key={text}>
        {title}
        {' '}
        {icon}
      </th>
    );
  });
};

const ShipsTable = ({ ships, ordering, toggleOrdering }) => {
  return (
    <Table striped bordered condensed hover>
      <thead>
        <tr>
          <ShipsTableHeaders ordering={ordering} toggleOrdering={toggleOrdering} />
        </tr>
      </thead>
      <tbody>
        {ships.map(ship => <ShipRow ship={ship} key={ship.name} />)}
      </tbody>
    </Table>
  );
};

export default ShipsTable;
