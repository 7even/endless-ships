import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses, OutfitLink } from '../../common';
import { sortByColumn } from '../../ordering';

const effectiveness = thruster => thruster.thrust / thruster.outfitSpace;

const Row = ({ thruster }) => (
  <tr>
    <TextCell><OutfitLink outfitName={thruster.name} /></TextCell>
    <NumberCell number={thruster.cost} />
    <NumberCell number={thruster.outfitSpace} />
    <NumberCell number={thruster.thrust} />
    <DecimalCell decimal={effectiveness(thruster)} />
    <NumberCell number={thruster.thrustingEnergy} />
    <NumberCell number={thruster.thrustingHeat} />
    <TextCell>{renderLicenses(thruster.licenses)}</TextCell>
  </tr>
);

const columns = new Map([
  ['Name',             R.prop('name')],
  ['Cost',             R.prop('cost')],
  ['Outfit sp.',       R.prop('outfitSpace')],
  ['Thrust',           R.prop('thrust')],
  ['Thrust per space', effectiveness],
  ['Thr. energy',      R.prop('thrustingEnergy')],
  ['Thr. heat',        R.prop('thrustingHeat')],
  ['Licenses',         null]
]);

const ThrustersTable = ({ thrusters, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {thrusters.map(thruster => <Row thruster={thruster} key={thruster.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    thrusters: sortByColumn(
      R.filter(R.has('thrust'), state.outfits),
      columns,
      state.outfitSettings.thrustersOrdering
    ),
    ordering: state.outfitSettings.thrustersOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-thrusters-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ThrustersTable);
