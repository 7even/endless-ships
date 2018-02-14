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

const TableHeaders = ({ columns, ordering, toggleOrdering }) => {
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

const thrustersHeaderColumns = [
  ['Name', 'name'],
  ['Cost', 'cost'],
  ['Outfit sp.', 'outfitSpace'],
  ['Thrust', 'thrust'],
  ['Thr. energy', 'thrustingEnergy'],
  ['Thr. heat', 'thrustingHeat'],
  ['Licenses']
];

let ThrustersTable = ({ thrusters, ordering, toggleOrdering }) => (
  <Table striped bordered condensed hover>
    <thead>
      <tr>
        <TableHeaders columns={thrustersHeaderColumns}
                      ordering={ordering}
                      toggleOrdering={toggleOrdering} />
      </tr>
    </thead>
    <tbody>
      {thrusters.map(thruster => <ThrusterRow thruster={thruster} key={thruster.name} />)}
    </tbody>
  </Table>
);

const mapStateToThrustersProps = (state) => {
  return {
    thrusters: sortOutfits(
      R.filter(R.has('thrust'), state.outfits),
      state.outfitSettings.thrustersOrdering
    ),
    ordering: state.outfitSettings.thrustersOrdering
  };
};

const mapDispatchToThrustersProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-thrusters-ordering', columnName });
    }
  };
};

ThrustersTable = connect(mapStateToThrustersProps, mapDispatchToThrustersProps)(ThrustersTable);

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

const steeringsHeaderColumns = [
  ['Name', 'name'],
  ['Cost', 'cost'],
  ['Outfit sp.', 'outfitSpace'],
  ['Turn', 'turn'],
  ['Turn. energy', 'turningEnergy'],
  ['Turn. heat', 'turningHeat'],
  ['Licenses']
];

let SteeringsTable = ({ steerings, ordering, toggleOrdering }) => (
  <Table striped bordered condensed hover>
    <thead>
      <tr>
        <TableHeaders columns={steeringsHeaderColumns}
                      ordering={ordering}
                      toggleOrdering={toggleOrdering} />
      </tr>
    </thead>
    <tbody>
      {steerings.map(steering => <SteeringRow steering={steering} key={steering.name} />)}
    </tbody>
  </Table>
);

const mapStateToSteeringsProps = (state) => {
  return {
    steerings: sortOutfits(
      R.filter(R.has('turn'), state.outfits),
      state.outfitSettings.steeringsOrdering
    ),
    ordering: state.outfitSettings.steeringsOrdering
  };
};

const mapDispatchToSteeringsProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-steerings-ordering', columnName });
    }
  };
};

SteeringsTable = connect(mapStateToSteeringsProps, mapDispatchToSteeringsProps)(SteeringsTable);

const OutfitsList = () => (
  <div className="app">
    <h2>Thrusters</h2>
    <ThrustersTable />

    <h2>Steerings</h2>
    <SteeringsTable />
  </div>
);

export default OutfitsList;
