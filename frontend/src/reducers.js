import R from 'ramda';

const initialState = {
  isLoading: true,
  shipSettings: {
    filtersCollapsed: true,
    ordering: { columnName: null }
  }
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
      shipSettings: {
        ...state.shipSettings,
        raceFilter:     initialRaceFilter(action.data.ships),
        categoryFilter: initialCategoryFilter(action.data.ships),
        licenseFilter:  initialLicenseFilter(action.data.ships)
      }
    };
  case 'toggle-ships-ordering':
    if (state.shipSettings.ordering.columnName === action.columnName) {
      if (state.shipSettings.ordering.order === 'asc') {
        return {
          ...state,
          shipSettings: { ...state.shipSettings, ordering: { columnName: null } }
        };
      } else {
        return {
          ...state,
          shipSettings: { ...state.shipSettings, ordering: { columnName: action.columnName, order: 'asc' } }
        };
      }
    } else {
      return {
        ...state,
        shipSettings: { ...state.shipSettings, ordering: { columnName: action.columnName, order: 'desc' } }
      };
    }
  case 'toggle-ship-filters-visibility':
    return {
      ...state,
      shipSettings: { ...state.shipSettings, filtersCollapsed: !state.shipSettings.filtersCollapsed }
    };
  case 'toggle-ships-race-filtering':
    return {
      ...state,
      shipSettings: {
        ...state.shipSettings,
        raceFilter: {
          ...state.shipSettings.raceFilter,
          [action.race]: !state.shipSettings.raceFilter[action.race]
        }
      }
    };
  case 'toggle-ships-category-filtering':
    return {
      ...state,
      shipSettings: {
        ...state.shipSettings,
        categoryFilter: {
          ...state.shipSettings.categoryFilter,
          [action.category]: !state.shipSettings.categoryFilter[action.category]
        }
      }
    };
  case 'toggle-ships-license-filtering':
    return {
      ...state,
      shipSettings: {
        ...state.shipSettings,
        licenseFilter: {
          ...state.shipSettings.licenseFilter,
          [action.license]: !state.shipSettings.licenseFilter[action.license]
        }
      }
    };
  default:
    return state;
  }
};

export default endlessShips;
