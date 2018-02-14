import React from 'react';
import { connect } from 'react-redux';
import { Table } from 'react-bootstrap';
import R from 'ramda';

import { FormattedNumber } from '../common';

const sortOutfits = (outfits, { columnName, order }) => {
  if (columnName) {
    const prop = R.propOr(0, columnName);
    const sortedProp = (order === 'asc') ? R.ascend(prop) : R.descend(prop);
    const comparator = R.sort(sortedProp);

    return comparator(outfits);
  } else {
    return outfits;
  }
};

const TextCell = ({ text }) => (
  <td className="text-left">{text}</td>
);

const NumberCell = ({ number }) => (
  <td className="text-right">
    <FormattedNumber number={number} />
  </td>
);

const ThrusterRow = ({ thruster }) => (
  <tr>
    <TextCell text={thruster.name} />
    <NumberCell number={thruster.cost} />
    <NumberCell number={-thruster.outfitSpace} />
    <NumberCell number={thruster.thrust} />
    <NumberCell number={thruster.thrustingEnergy} />
    <NumberCell number={thruster.thrustingHeat} />
    <TextCell text={thruster.licenses} />
  </tr>
);

let ThrustersTable = ({ thrusters }) => (
  <Table striped bordered condensed hover>
    <thead>
      <tr>
        <th className="text-center">Name</th>
        <th className="text-center">Cost</th>
        <th className="text-center">Outfit sp.</th>
        <th className="text-center">Thrust</th>
        <th className="text-center">Thr. energy</th>
        <th className="text-center">Thr. heat</th>
        <th className="text-center">Licenses</th>
      </tr>
    </thead>
    <tbody>
      {thrusters.map(thruster => <ThrusterRow thruster={thruster} key={thruster.name} />)}
    </tbody>
  </Table>
);

const mapThrustersStateToProps = (state) => {
  return {
    thrusters: sortOutfits(
      R.filter(R.has('thrust'), state.outfits),
      state.outfitSettings.thrustersOrdering
    )
  };
};

ThrustersTable = connect(mapThrustersStateToProps)(ThrustersTable);

const SteeringRow = ({ steering }) => (
  <tr>
    <TextCell text={steering.name} />
    <NumberCell number={steering.cost} />
    <NumberCell number={-steering.outfitSpace} />
    <NumberCell number={steering.turn} />
    <NumberCell number={steering.turningEnergy} />
    <NumberCell number={steering.turningHeat} />
    <TextCell text={steering.licenses} />
  </tr>
);

let SteeringsTable = ({ steerings }) => (
  <Table striped bordered condensed hover>
    <thead>
      <tr>
        <th className="text-center">Name</th>
        <th className="text-center">Cost</th>
        <th className="text-center">Outfit sp.</th>
        <th className="text-center">Turn</th>
        <th className="text-center">Turn. energy</th>
        <th className="text-center">Turn. heat</th>
        <th className="text-center">Licenses</th>
      </tr>
    </thead>
    <tbody>
      {steerings.map(steering => <SteeringRow steering={steering} key={steering.name} />)}
    </tbody>
  </Table>
);

const mapSteeringsStateToProps = (state) => {
  return {
    steerings: sortOutfits(
      R.filter(R.has('turn'), state.outfits),
      state.outfitSettings.steeringsOrdering
    )
  };
};

SteeringsTable = connect(mapSteeringsStateToProps)(SteeringsTable);

const OutfitsList = () => (
  <div className="app">
    <h2>Thrusters</h2>
    <ThrustersTable />

    <h2>Steerings</h2>
    <SteeringsTable />
  </div>
);

export default OutfitsList;
