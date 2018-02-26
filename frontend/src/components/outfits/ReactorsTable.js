import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const totalEnergyGeneration = (reactor) => {
  return R.propOr(0, 'energyGeneration', reactor) + R.propOr(0, 'solarCollection', reactor);
};

const effectiveness = reactor => totalEnergyGeneration(reactor) / reactor.outfitSpace;

const Row = ({ reactor }) => (
  <tr>
    <TextCell>{reactor.name}</TextCell>
    <NumberCell number={reactor.cost} />
    <NumberCell number={reactor.outfitSpace} />
    <NumberCell number={totalEnergyGeneration(reactor)} />
    <DecimalCell decimal={effectiveness(reactor)} />
    <NumberCell number={reactor.heatGeneration} />
    <TextCell>{renderLicenses(reactor.licenses)}</TextCell>
  </tr>
);

const columns = new Map([
  ['Name',              R.prop('name')],
  ['Cost',              R.prop('cost')],
  ['Outfit sp.',        R.prop('outfitSpace')],
  ['Energy generation', totalEnergyGeneration],
  ['Energy per space',  effectiveness],
  ['Heat generation',   R.propOr(0, 'heatGeneration')],
  ['Licenses',          null]
]);

const ReactorsTable = ({ reactors, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {reactors.map(reactor => <Row reactor={reactor} key={reactor.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    reactors: sortByColumn(
      R.filter(
        outfit => R.or(R.has('energyGeneration', outfit), R.has('solarCollection', outfit)),
        state.outfits
      ),
      columns,
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
