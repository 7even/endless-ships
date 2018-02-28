import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses } from '../../common';
import { sortByColumn } from '../../ordering';

const shieldDamagePerOutfitSpace = gun => gun.weapon.shieldDamage.perSecond / gun.outfitSpace;
const hullDamagePerOutfitSpace   = gun => gun.weapon.hullDamage.perSecond / gun.outfitSpace;

const Row = ({ gun }) => {
  return (
    <tr>
      <TextCell>{gun.name}</TextCell>
      <NumberCell number={gun.cost} />
      <NumberCell number={gun.outfitSpace} />
      <NumberCell number={gun.weapon.shieldDamage.perSecond} />
      <DecimalCell decimal={shieldDamagePerOutfitSpace(gun)} />
      <NumberCell number={gun.weapon.hullDamage.perSecond} />
      <DecimalCell decimal={hullDamagePerOutfitSpace(gun)} />
      <NumberCell number={gun.weapon.range}/>
      <TextCell>{gun.weapon.shotsPerSecond}</TextCell>
      <TextCell>{renderLicenses(gun.licenses)}</TextCell>
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

const GunsTable = ({ guns, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {guns.map(gun => <Row gun={gun} key={gun.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    guns: sortByColumn(
      R.filter(R.propEq('category', 'Guns'), state.outfits),
      columns,
      state.outfitSettings.gunsOrdering
    ),
    ordering: state.outfitSettings.gunsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-guns-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(GunsTable);
