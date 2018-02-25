import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const totalCooling = cooler => R.propOr(0, 'cooling', cooler) + R.propOr(0, 'activeCooling', cooler);

const Row = ({ cooler }) => (
  <tr>
    <TextCell>{cooler.name}</TextCell>
    <NumberCell number={cooler.cost} />
    <NumberCell number={cooler.outfitSpace} />
    <NumberCell number={totalCooling(cooler)} />
    <NumberCell number={cooler.coolingEnergy} />
    <TextCell>{renderLicenses(cooler.licenses)}</TextCell>
  </tr>
);

const columns = new Map([
  ['Name',           R.prop('name')],
  ['Cost',           R.prop('cost')],
  ['Outfit sp.',     R.prop('outfitSpace')],
  ['Cooling',        totalCooling],
  ['Cooling energy', R.propOr(0, 'coolingEnergy')],
  ['Licenses',       null]
]);

const CoolersTable = ({ coolers, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {coolers.map(cooler => <Row cooler={cooler} key={cooler.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    coolers: sortByColumn(
      R.filter(
        outfit => R.and(
          R.or(
            R.has('cooling', outfit),
            R.has('activeCooling', outfit)
          ),
          R.propEq('category', 'Systems', outfit)
        ),
        state.outfits
      ),
      columns,
      state.outfitSettings.coolersOrdering
    ),
    ordering: state.outfitSettings.coolersOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-coolers-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(CoolersTable);
