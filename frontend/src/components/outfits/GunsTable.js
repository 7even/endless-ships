import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses, OutfitLink, orZero, damage, damagePerOutfitSpace } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ gun }) => {
  return (
    <tr>
      <TextCell><OutfitLink outfitName={gun.name} /></TextCell>
      <NumberCell number={gun.cost} />
      <NumberCell number={gun.outfitSpace} />
      <NumberCell number={damage('shieldDamage', gun)} />
      <DecimalCell decimal={damagePerOutfitSpace('shieldDamage', gun)} />
      <NumberCell number={damage('hullDamage', gun)} />
      <DecimalCell decimal={damagePerOutfitSpace('hullDamage', gun)} />
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
  ['Shield damage',         orZero(damage('shieldDamage'))],
  ['Shield damage / space', damagePerOutfitSpace('shieldDamage')],
  ['Hull damage',           orZero(damage('hullDamage'))],
  ['Hull damage / space',   damagePerOutfitSpace('hullDamage')],
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
