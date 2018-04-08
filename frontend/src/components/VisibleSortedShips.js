import { connect } from 'react-redux';
import R from 'ramda';

import ShipsTable from './ShipsTable';
import { sortByColumn } from '../ordering';
import { totalShipCost } from '../common';

const columns = new Map([
  ['Name',         R.prop('name')],
  ['Race',         null],
  ['Cost',         totalShipCost],
  ['Category',     null],
  ['Hull',         R.propOr(0, 'hull')],
  ['Shields',      R.propOr(0, 'shields')],
  ['Mass',         R.propOr(0, 'mass')],
  ['Engine cap.',  R.propOr(0, 'engineCapacity')],
  ['Weapon cap.',  R.propOr(0, 'weaponCapacity')],
  ['Fuel cap.',    R.propOr(0, 'fuelCapacity')],
  ['Outfit sp.',   R.propOr(0, 'outfitSpace')],
  ['Cargo sp.',    R.propOr(0, 'cargoSpace')],
  ['Crew / bunks', R.propOr(0, 'bunks')],
  ['Licenses',     null]
]);

const filterShips = (ships, raceFilter, categoryFilter, licenseFilter) => {
  const filters = [
    ship => raceFilter[ship.race],
    ship => categoryFilter[ship.category],
    ship => R.none(license => !licenseFilter[license])(ship.licenses || [])
  ];

  return R.filter(R.allPass(filters), ships);
};

const mapStateToProps = (state) => {
  return {
    ships: sortByColumn(
      filterShips(
        state.ships,
        state.shipSettings.raceFilter,
        state.shipSettings.categoryFilter,
        state.shipSettings.licenseFilter
      ),
      columns,
      state.shipSettings.ordering
    ),
    headers: columns,
    ordering: state.shipSettings.ordering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-ships-ordering', columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShipsTable);
