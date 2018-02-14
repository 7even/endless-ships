import { connect } from 'react-redux';
import R from 'ramda';

import ShipsTable from './ShipsTable';
import { sortByColumn } from '../ordering';

const filterShips = (ships, raceFilter, categoryFilter, licenseFilter) => {
  const filters = [
    ship => raceFilter[ship.race],
    ship => categoryFilter[ship.category],
    ship => R.none(license => !licenseFilter[license])(ship.licenses)
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
      state.shipSettings.ordering
    ),
    ordering: state.shipSettings.ordering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-ships-ordering', columnName: columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShipsTable);
