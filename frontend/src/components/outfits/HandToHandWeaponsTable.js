import React from 'react';
import { connect } from 'react-redux';
import R from 'ramda';

import Table, { TextCell, NumberCell } from '../Table';
import { OutfitLink } from '../../common';
import { sortByColumn } from '../../ordering';

const Row = ({ weapon }) => {
  return (
    <tr>
      <TextCell><OutfitLink outfit={weapon} /></TextCell>
      <NumberCell number={weapon.cost} />
      <NumberCell number={weapon.captureAttack} />
      <NumberCell number={weapon.captureDefense} />
      <NumberCell number={weapon.illegal} />
    </tr>
  );
};

const columns = new Map([
  ['Name',            R.prop('name')],
  ['Cost',            R.propOr(0, 'cost')],
  ['Capture attack',  R.propOr(0, 'captureAttack')],
  ['Capture defense', R.prop('captureDefense')],
  ['Illegal',         R.propOr(0, 'illegal')]
]);

const HandToHandWeaponsTable = ({ weapons, ordering, toggleOrdering }) => (
  <Table headerColumns={columns}
         ordering={ordering}
         toggleOrdering={toggleOrdering}>
    {weapons.map(weapon => <Row weapon={weapon} key={weapon.name} />)}
  </Table>
);

const mapStateToProps = (state) => {
  return {
    weapons: sortByColumn(
      R.filter(R.propEq('category', 'Hand to Hand'), state.outfits),
      columns,
      state.outfitSettings.handToHandWeaponsOrdering
    ),
    ordering: state.outfitSettings.handToHandWeaponsOrdering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-hand-to-hand-weapons-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(HandToHandWeaponsTable);
