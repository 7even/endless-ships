import React from 'react';
import { connect } from 'react-redux';
import { Table } from 'react-bootstrap';
import R from 'ramda';

const ThrusterRow = ({ thruster }) => (
  <tr>
    <td>{thruster.name}</td>
    <td>{thruster.cost}</td>
    <td>{-thruster.outfitSpace}</td>
    <td>{thruster.thrust}</td>
    <td>{thruster.thrustingEnergy}</td>
    <td>{thruster.thrustingHeat}</td>
    <td>{thruster.licenses}</td>
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
  const thrusters = R.filter(
    outfit => R.has('thrust')(outfit),
    state.outfits
  );

  return {
    thrusters: thrusters
  };
};

ThrustersTable = connect(mapThrustersStateToProps)(ThrustersTable);

const SteeringRow = ({ steering }) => (
  <tr>
    <td>{steering.name}</td>
    <td>{steering.cost}</td>
    <td>{-steering.outfitSpace}</td>
    <td>{steering.turn}</td>
    <td>{steering.turningEnergy}</td>
    <td>{steering.turningHeat}</td>
    <td>{steering.licenses}</td>
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
  const steerings = R.filter(
    outfit => R.has('turn')(outfit),
    state.outfits
  );

  return {
    steerings: steerings
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
