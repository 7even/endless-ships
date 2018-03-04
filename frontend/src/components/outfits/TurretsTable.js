import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell, DecimalCell } from '../Table';
import { renderLicenses, OutfitLink } from '../../common';
import { sortByColumn } from '../../ordering';

const shieldDamagePerOutfitSpace = gun => gun.weapon.shieldDamage.perSecond / gun.outfitSpace;
const hullDamagePerOutfitSpace   = gun => gun.weapon.hullDamage.perSecond / gun.outfitSpace;

const Row = ({ turret }) => {
  return (
    <tr>
      <TextCell><OutfitLink outfitName={turret.name} /></TextCell>
      <NumberCell number={turret.cost} />
      <NumberCell number={turret.outfitSpace} />
      <NumberCell number={turret.weapon.shieldDamage.perSecond} />
      <DecimalCell decimal={shieldDamagePerOutfitSpace(turret)} />
      <NumberCell number={turret.weapon.hullDamage.perSecond} />
      <DecimalCell decimal={hullDamagePerOutfitSpace(turret)} />
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
  ['Shield damage',         R.path(['weapon', 'shieldDamage', 'perSecond'])],
  ['Shield damage / space', shieldDamagePerOutfitSpace],
  ['Hull damage',           R.path(['weapon', 'hullDamage', 'perSecond'])],
  ['Hull damage / space',   hullDamagePerOutfitSpace],
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
      R.path(['weapon', 'shieldDamage', 'perSecond'], turret),
      R.path(['weapon', 'hullDamage', 'perSecond'], turret)
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
