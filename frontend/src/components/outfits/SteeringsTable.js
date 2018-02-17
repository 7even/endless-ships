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
  <Table headerColumns={steeringsHeaderColumns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {steerings.map(steering => <Row steering={steering} key={steering.name} />)}
  </Table>
);

const mapStateToSteeringsProps = (state) => {
  return {
    steerings: sortByColumn(
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

export default connect(
  mapStateToSteeringsProps,
  mapDispatchToSteeringsProps
)(SteeringsTable);
