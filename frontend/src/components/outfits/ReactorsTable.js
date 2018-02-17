import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ reactor }) => (
  <tr>
    <TextCell>{reactor.name}</TextCell>
    <NumberCell number={reactor.cost} />
    <NumberCell number={reactor.outfitSpace} />
    <NumberCell number={reactor.energyGeneration} />
    <NumberCell number={reactor.heatGeneration} />
    <TextCell>{renderLicenses(reactor.licenses)}</TextCell>
  </tr>
);

const headerColumns = [
  ['Name', 'name'],
  ['Cost', 'cost'],
  ['Outfit sp.', 'outfitSpace'],
  ['Energy generation', 'energyGeneration'],
  ['Heat generation', 'heatGeneration'],
  ['Licenses']
];

const ReactorsTable = ({ reactors, ordering, toggleOrdering }) => (
  <Table headerColumns={headerColumns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {reactors.map(reactor => <Row reactor={reactor} key={reactor.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    reactors: sortByColumn(
      R.filter(R.has('energyGeneration'), state.outfits),
      state.outfitSettings.reactorsOrdering
    ),
    ordering: state.outfitSettings.reactorsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-reactors-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ReactorsTable);
