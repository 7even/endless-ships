import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const effectiveness = module => module.hullRepairRate / module.outfitSpace;

const Row = ({ hullRepairModule }) => (
  <tr>
    <TextCell>{hullRepairModule.name}</TextCell>
    <NumberCell number={hullRepairModule.cost} />
    <NumberCell number={hullRepairModule.outfitSpace} />
    <NumberCell number={hullRepairModule.hullRepairRate} />
    <DecimalCell decimal={effectiveness(hullRepairModule)} />
    <NumberCell number={hullRepairModule.hullEnergy} />
    <NumberCell number={hullRepairModule.hullHeat} />
    <TextCell>{renderLicenses(hullRepairModule.licenses)}</TextCell>
  </tr>
);

const columns = new Map([
  ['Name',             R.prop('name')],
  ['Cost',             R.prop('cost')],
  ['Outfit sp.',       R.prop('outfitSpace')],
  ['Hull repair rate', R.prop('hullRepairRate')],
  ['Hull per space',   effectiveness],
  ['Hull energy',      R.prop('hullEnergy')],
  ['Hull heat',        R.propOr(0, 'hullHeat')],
  ['Licenses',         null]
]);

const HullRepairModulesTable = ({ hullRepairModules, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {hullRepairModules.map(module => <Row hullRepairModule={module} key={module.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    hullRepairModules: sortByColumn(
      R.filter(R.has('hullRepairRate'), state.outfits),
      columns,
      state.outfitSettings.hullRepairModulesOrdering
    ),
    ordering: state.outfitSettings.hullRepairModulesOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-hull-repair-modules-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(HullRepairModulesTable);
