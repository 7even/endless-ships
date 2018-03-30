import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses, OutfitLink, orZero, damage, damagePerOutfitSpace } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ secondaryWeapon }) => {
  return (
    <tr>
      <TextCell><OutfitLink outfitName={secondaryWeapon.name} /></TextCell>
      <NumberCell number={secondaryWeapon.cost} />
      <NumberCell number={secondaryWeapon.outfitSpace} />
      <NumberCell number={damage('shieldDamage', secondaryWeapon)} />
      <DecimalCell decimal={damagePerOutfitSpace('shieldDamage', secondaryWeapon)} />
      <NumberCell number={damage('hullDamage', secondaryWeapon)} />
      <DecimalCell decimal={damagePerOutfitSpace('hullDamage', secondaryWeapon)} />
      <NumberCell number={secondaryWeapon.weapon.range}/>
      <TextCell>{secondaryWeapon.weapon.shotsPerSecond}</TextCell>
      <TextCell>{renderLicenses(secondaryWeapon.licenses)}</TextCell>
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

const SecondaryWeaponsTable = ({ secondaryWeapons, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {secondaryWeapons.map(weapon => <Row secondaryWeapon={weapon} key={weapon.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    secondaryWeapons: sortByColumn(
      R.filter(R.propEq('category', 'Secondary Weapons'), state.outfits),
      columns,
      state.outfitSettings.secondaryWeaponsOrdering
    ),
    ordering: state.outfitSettings.secondaryWeaponsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-secondary-weapons-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(SecondaryWeaponsTable);
