import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ battery }) => (
  <tr>
    <TextCell>{battery.name}</TextCell>
    <NumberCell number={battery.cost} />
    <NumberCell number={battery.outfitSpace} />
    <NumberCell number={battery.energyCapacity} />
    <TextCell>{renderLicenses(battery.licenses)}</TextCell>
  </tr>
);

const columns = new Map([
  ['Name',            R.prop('name')],
  ['Cost',            R.prop('cost')],
  ['Outfit sp.',      R.prop('outfitSpace')],
  ['Energy capacity', R.prop('energyCapacity')],
  ['Licenses',        null]
]);

const BatteriesTable = ({ batteries, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {batteries.map(battery => <Row battery={battery} key={battery.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    batteries: sortByColumn(
      R.filter(
        outfit => R.and(
          R.has('energyCapacity', outfit),
          R.or(
            R.propEq('category', 'Power', outfit),
            R.propEq('category', 'Systems', outfit)
          )
        ),
        state.outfits
      ),
      columns,
      state.outfitSettings.batteriesOrdering
    ),
    headers: columns,
    ordering: state.outfitSettings.batteriesOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-batteries-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(BatteriesTable);
