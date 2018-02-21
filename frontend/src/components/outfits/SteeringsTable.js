import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ steering }) => (
  <tr>
    <TextCell>{steering.name}</TextCell>
    <NumberCell number={steering.cost} />
    <NumberCell number={steering.outfitSpace} />
    <NumberCell number={steering.turn} />
    <NumberCell number={steering.turningEnergy} />
    <NumberCell number={steering.turningHeat} />
    <TextCell>{renderLicenses(steering.licenses)}</TextCell>
  </tr>
);

const columns = new Map([
  ['Name',         R.prop('name')],
  ['Cost',         R.prop('cost')],
  ['Outfit sp.',   R.prop('outfitSpace')],
  ['Turn',         R.prop('turn')],
  ['Turn. energy', R.prop('turningEnergy')],
  ['Turn. heat',   R.prop('turningHeat')],
  ['Licenses',     null]
]);

const SteeringsTable = ({ steerings, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {steerings.map(steering => <Row steering={steering} key={steering.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    steerings: sortByColumn(
      R.filter(R.has('turn'), state.outfits),
      columns,
      state.outfitSettings.steeringsOrdering
    ),
    headers: columns,
    ordering: state.outfitSettings.steeringsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-steerings-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(SteeringsTable);
