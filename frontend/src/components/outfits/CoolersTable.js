import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ cooler }) => (
  <tr>
    <TextCell>{cooler.name}</TextCell>
    <NumberCell number={cooler.cost} />
    <NumberCell number={cooler.outfitSpace} />
    <NumberCell number={cooler.cooling} />
    <NumberCell number={cooler.activeCooling} />
    <NumberCell number={cooler.coolingEnergy} />
    <TextCell>{renderLicenses(cooler.licenses)}</TextCell>
  </tr>
);

const headerColumns = [
  ['Name', 'name'],
  ['Cost', 'cost'],
  ['Outfit sp.', 'outfitSpace'],
  ['Cooling', 'cooling'],
  ['Active cooling', 'activeCooling'],
  ['Cooling energy', 'coolingEnergy'],
  ['Licenses']
];

const CoolersTable = ({ coolers, ordering, toggleOrdering }) => (
  <Table headerColumns={headerColumns}
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
