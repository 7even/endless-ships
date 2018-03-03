import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { renderLicenses, OutfitLink } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ turret }) => {
  return (
    <tr>
      <TextCell><OutfitLink outfit={turret} /></TextCell>
      <NumberCell number={turret.cost} />
      <NumberCell number={turret.outfitSpace} />
      <NumberCell number={turret.weapon.antiMissile} />
      <NumberCell number={turret.weapon.range}/>
      <TextCell>{turret.weapon.shotsPerSecond}</TextCell>
      <TextCell>{renderLicenses(turret.licenses)}</TextCell>
    </tr>
  );
};

const columns = new Map([
  ['Name',         R.prop('name')],
  ['Cost',         R.prop('cost')],
  ['Outfit sp.',   R.prop('outfitSpace')],
  ['Anti-missile', R.path(['weapon', 'antiMissile'])],
  ['Range',        R.path(['weapon', 'range'])],
  ['Fire rate',    null],
  ['Licenses',     null]
]);

const AntiMissileTurretsTable = ({ turrets, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {turrets.map(turret => <Row turret={turret} key={turret.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    turrets: sortByColumn(
      R.filter(R.path(['weapon', 'antiMissile']), state.outfits),
      columns,
      state.outfitSettings.antiMissileTurretsOrdering
    ),
    ordering: state.outfitSettings.antiMissileTurretsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-anti-missile-turrets-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(AntiMissileTurretsTable);
