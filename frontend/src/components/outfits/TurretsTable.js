import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses, OutfitLink, orZero, damage, damagePerOutfitSpace } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ turret }) => {
  return (
    <tr>
      <TextCell><OutfitLink outfitName={turret.name} /></TextCell>
      <NumberCell number={turret.cost} />
      <NumberCell number={turret.outfitSpace} />
      <NumberCell number={damage('shieldDamage', turret)} />
      <DecimalCell decimal={damagePerOutfitSpace('shieldDamage', turret)} />
      <NumberCell number={damage('hullDamage', turret)} />
      <DecimalCell decimal={damagePerOutfitSpace('hullDamage', turret)} />
      <NumberCell number={turret.weapon.range}/>
      <TextCell>{turret.weapon.shotsPerSecond}</TextCell>
      <TextCell>{renderLicenses(turret.licenses)}</TextCell>
    </tr>
  );
};

const columns = new Map([
  ['Name',                  R.prop('name')],
  ['Cost',                  R.prop('cost')],
  ['Outfit sp.',            R.prop('outfitSpace')],
  ['Shield damage',         orZero(damage('shieldDamage'))],
  ['Shield damage / space', damagePerOutfitSpace('shieldDamage')],
  ['Hull damage',           orZero(damage('hullDamage'))],
  ['Hull damage / space',   damagePerOutfitSpace('hullDamage')],
  ['Range',                 R.path(['weapon', 'range'])],
  ['Fire rate',             null],
  ['Licenses',              null]
]);

const TurretsTable = ({ turrets, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {turrets.map(turret => <Row turret={turret} key={turret.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  const filterAndSortTurrets = R.pipe(
    R.filter(R.propEq('category', 'Turrets')),
    R.filter(turret => R.or(
      damage('shieldDamage', turret),
      damage('hullDamage', turret)
    )),
    R.partialRight(sortByColumn, [columns, state.outfitSettings.turretsOrdering])
  );

  return {
    turrets:  filterAndSortTurrets(state.outfits),
    ordering: state.outfitSettings.turretsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-turrets-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(TurretsTable);
