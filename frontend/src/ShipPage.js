import React from 'react';
import { Link } from 'react-router-dom';
import { Panel } from 'react-bootstrap';
import NumberFormat from 'react-number-format';

function FormattedNumber(props) {
  return (
    <NumberFormat value={props.number}
                  displayType={'text'}
                  thousandSeparator={true} />
  );
}

const ShipPage = ({ ship }) => {
  console.log(ship);
  return (
    <div className="app">
      <ol className="breadcrumb">
        <li><Link to="/">Ships</Link></li>
        <li className="active">{ship.name}</li>
      </ol>

      <Panel>
        <Panel.Heading>{ship.name}</Panel.Heading>

        <Panel.Body>
          <ul>
            <li>cost: <FormattedNumber number={ship.cost} /></li>
            <li>shields: <FormattedNumber number={ship.shields} /></li>
            <li>hull: <FormattedNumber number={ship.hull} /></li>
            <li>mass: <FormattedNumber number={ship.mass} /></li>
            <li>cargo space: <FormattedNumber number={ship.cargoSpace} /></li>
            <li>required crew: <FormattedNumber number={ship.requiredCrew} /></li>
            <li>bunks: <FormattedNumber number={ship.bunks} /></li>
            <li>fuel capacity: <FormattedNumber number={ship.fuelCapacity} /></li>
            <li>outfit space: <FormattedNumber number={ship.outfitSpace} /></li>
            <li>weapon capacity: <FormattedNumber number={ship.weaponCapacity} /></li>
            <li>engine capacity: <FormattedNumber number={ship.engineCapacity} /></li>
          </ul>
        </Panel.Body>
      </Panel>

      {ship.description.map((line, index) => (
        <p key={`${ship.name}-description-${index}`}>{line}</p>
      ))}
    </div>
  );
};

export default ShipPage;
