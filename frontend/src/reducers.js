import R from 'ramda';

const initialState = {
  isLoading: true,
  filtersCollapsed: true,
  ordering: { columnName: null }
};

const initialRaceFilter = (ships) => R.uniq(ships.map(ship => ship.race)).reduce(
  (races, race) => R.merge(races, { [race]: true }),
  {}
);

const initialCategoryFilter = (ships) => R.uniq(ships.map(ship => ship.category)).reduce(
  (categories, category) => R.merge(categories, { [category]: true }),
  {}
);

const initialLicenseFilter = (ships) => R.uniq(R.flatten(ships.map(ship => ship.licenses))).reduce(
  (licenses, license) => R.merge(licenses, { [license]: true }),
  {}
);

const endlessShips = (state = initialState, action) => {
  console.log(`Received action:`);
  console.log(action);

  switch (action.type) {
  case 'load-data':
    return {
      ...state,
      isLoading:      false,
      ships:          action.data.ships,
      outfits:        action.data.outfits,
      raceFilter:     initialRaceFilter(action.data.ships),
      categoryFilter: initialCategoryFilter(action.data.ships),
      licenseFilter:  initialLicenseFilter(action.data.ships)
    };
  case 'toggle-ordering':
    if (state.ordering.columnName === action.columnName) {
      if (state.ordering.order === 'asc') {
        return {
          ...state,
          ordering: { columnName: null }
        };
      } else {
        return {
          ...state,
          ordering: { columnName: action.columnName, order: 'asc' }
        };
      }
    } else {
      return {
        ...state,
        ordering: { columnName: action.columnName, order: 'desc' }
      };
    }
  case 'toggle-filters-visibility':
    return {
      ...state,
      filtersCollapsed: !state.filtersCollapsed
    };
  case 'toggle-race-filtering':
    return {
      ...state,
      raceFilter: {
        ...state.raceFilter,
        [action.race]: !state.raceFilter[action.race]
      }
    };
  case 'toggle-category-filtering':
    return {
      ...state,
      categoryFilter: {
        ...state.categoryFilter,
        [action.category]: !state.categoryFilter[action.category]
      }
    };
  case 'toggle-license-filtering':
    return {
      ...state,
      licenseFilter: {
        ...state.licenseFilter,
        [action.license]: !state.licenseFilter[action.license]
      }
    };
  default:
    return state;
  }
};

export default endlessShips;
