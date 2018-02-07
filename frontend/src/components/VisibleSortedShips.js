import { connect } from 'react-redux';
import R from 'ramda';
import ShipsTable from './ShipsTable';

const filterShips = (ships, raceFilter, categoryFilter, licenseFilter) => {
  const filters = [
    ship => raceFilter[ship.race],
    ship => categoryFilter[ship.category],
    ship => R.none(license => !licenseFilter[license])(ship.licenses)
  ];

  return R.filter(R.allPass(filters), ships);
};

const sortShips = (ships, { columnName, order }) => {
  const prop = R.propOr(0, columnName);
  const sortedProp = (order === 'asc') ? R.ascend(prop) : R.descend(prop);
  const comparator = R.sort(sortedProp);

  return comparator(ships);
};

const mapStateToProps = (state) => {
  return {
    ships: sortShips(
      filterShips(state.data, state.raceFilter, state.categoryFilter, state.licenseFilter),
      state.ordering
    ),
    ordering: state.ordering
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    toggleOrdering: (columnName) => {
      dispatch({ type: 'toggle-ordering', columnName: columnName });
    }
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(ShipsTable);
