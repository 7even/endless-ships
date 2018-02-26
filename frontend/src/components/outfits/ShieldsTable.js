import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const effectiveness = generator => generator.shieldGeneration / generator.outfitSpace;

const Row = ({ shieldGenerator }) => (
  <tr>
    <TextCell>{shieldGenerator.name}</TextCell>
    <NumberCell number={shieldGenerator.cost} />
    <NumberCell number={shieldGenerator.outfitSpace} />
    <NumberCell number={shieldGenerator.shieldGeneration} />
    <DecimalCell decimal={effectiveness(shieldGenerator)} />
    <NumberCell number={shieldGenerator.shieldEnergy} />
    <NumberCell number={shieldGenerator.shieldHeat} />
    <TextCell>{renderLicenses(shieldGenerator.licenses)}</TextCell>
  </tr>
);

const columns = new Map([
  ['Name',              R.prop('name')],
  ['Cost',              R.prop('cost')],
  ['Outfit sp.',        R.prop('outfitSpace')],
  ['Shield generation', R.prop('shieldGeneration')],
  ['Shield per space',  effectiveness],
  ['Shield energy',     R.prop('shieldEnergy')],
  ['Shield heat',       R.propOr(0, 'shieldHeat')],
  ['Licenses',          null]
]);

const ShieldGeneratorsTable = ({ shieldGenerators, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {shieldGenerators.map(generator => <Row shieldGenerator={generator} key={generator.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    shieldGenerators: sortByColumn(
      R.filter(R.has('shieldGeneration'), state.outfits),
      columns,
      state.outfitSettings.shieldGeneratorsOrdering
    ),
    ordering: state.outfitSettings.shieldGeneratorsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-shield-generators-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShieldGeneratorsTable);
