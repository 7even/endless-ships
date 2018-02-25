import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ ramscoop }) => (
  <tr>
    <TextCell>{ramscoop.name}</TextCell>
    <NumberCell number={ramscoop.cost} />
    <NumberCell number={ramscoop.outfitSpace} />
    <NumberCell number={ramscoop.ramscoop} />
    <TextCell>{renderLicenses(ramscoop.licenses)}</TextCell>
  </tr>
);

const columns = new Map([
  ['Name',       R.prop('name')],
  ['Cost',       R.prop('cost')],
  ['Outfit sp.', R.prop('outfitSpace')],
  ['Ramscoop',   R.prop('ramscoop')],
  ['Licenses',   null]
]);

const RamscoopsTable = ({ ramscoops, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {ramscoops.map(ramscoop => <Row ramscoop={ramscoop} key={ramscoop.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    ramscoops: sortByColumn(
      R.filter(R.has('ramscoop'), state.outfits),
      columns,
      state.outfitSettings.ramscoopsOrdering
    ),
    ordering: state.outfitSettings.ramscoopsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-ramscoops-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(RamscoopsTable);
