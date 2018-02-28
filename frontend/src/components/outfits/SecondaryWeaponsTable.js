import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const shieldDamagePerOutfitSpace = secondaryWeapon => secondaryWeapon.weapon.shieldDamage.perSecond / secondaryWeapon.outfitSpace;
const hullDamagePerOutfitSpace   = secondaryWeapon => secondaryWeapon.weapon.hullDamage.perSecond / secondaryWeapon.outfitSpace;

const Row = ({ secondaryWeapon }) => {
  return (
    <tr>
      <TextCell>{secondaryWeapon.name}</TextCell>
      <NumberCell number={secondaryWeapon.cost} />
      <NumberCell number={secondaryWeapon.outfitSpace} />
      <NumberCell number={secondaryWeapon.weapon.shieldDamage.perSecond} />
      <DecimalCell decimal={shieldDamagePerOutfitSpace(secondaryWeapon)} />
      <NumberCell number={secondaryWeapon.weapon.hullDamage.perSecond} />
      <DecimalCell decimal={hullDamagePerOutfitSpace(secondaryWeapon)} />
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
  ['Shield damage',         R.path(['weapon', 'shieldDamage', 'perSecond'])],
  ['Shield damage / space', shieldDamagePerOutfitSpace],
  ['Hull damage',           R.path(['weapon', 'hullDamage', 'perSecond'])],
  ['Hull damage / space',   hullDamagePerOutfitSpace],
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
