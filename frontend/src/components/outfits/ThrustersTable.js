import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ thruster }) => (
  <tr>
    <TextCell>{thruster.name}</TextCell>
    <NumberCell number={thruster.cost} />
    <NumberCell number={thruster.outfitSpace} />
    <NumberCell number={thruster.thrust} />
    <NumberCell number={thruster.thrustingEnergy} />
    <NumberCell number={thruster.thrustingHeat} />
    <TextCell>{renderLicenses(thruster.licenses)}</TextCell>
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

const ThrustersTable = ({ thrusters, ordering, toggleOrdering }) => (
  <Table headerColumns={thrustersHeaderColumns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {thrusters.map(thruster => <Row thruster={thruster} key={thruster.name} />)}
  </Table>
);

const mapStateToThrustersProps = (state) => {
  return {
    thrusters: sortByColumn(
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

export default connect(
  mapStateToThrustersProps,
  mapDispatchToThrustersProps
)(ThrustersTable);
