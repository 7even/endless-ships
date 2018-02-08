import R from 'ramda';

const races = [
  'human',
  'hai',
  'quarg',
  'korath',
  'wanderer',
  'coalition',
  'pug',
  'pirate',
  'remnant',
  'drak'
];

const categories = [
  'Transport',
  'Light Freighter',
  'Heavy Freighter',
  'Interceptor',
  'Light Warship',
  'Medium Warship',
  'Heavy Warship',
  'Fighter',
  'Drone'
];

const licenses = {
  'Navy':               'human',
  'Carrier':            'human',
  'Cruiser':            'human',
  'Militia Carrier':    'human',
  'Unfettered Militia': 'hai',
  'Wanderer':           'wanderer',
  'Wanderer Military':  'wanderer',
  'Coalition':          'coalition',
  'Heliarch':           'coalition',
  'Remnant':            'remnant'
};

const initialState = {
  isLoading: true,
  data: {},
  filtersCollapsed: true,
  ordering: { columnName: null },
  raceFilter: races.reduce(
    (races, race) => R.merge(races, { [race]: true }),
    {}
  ),
  categoryFilter: categories.reduce(
    (categories, category) => R.merge(categories, { [category]: true }),
    {}
  ),
  licenseFilter: Object.keys(licenses).reduce(
    (licenses, license) => R.merge(licenses, { [license]: true }),
    {}
  )
};

const endlessShips = (state = initialState, action) => {
  console.log(`Received action:`);
  console.log(action);

  switch (action.type) {
  case 'load-data':
    return { ...state, isLoading: false, data: action.data };
  case 'toggle-ordering':
    if (state.ordering.columnName === action.columnName) {
      if (state.ordering.order === 'asc') {
        return { ...state, ordering: { columnName: null } };
      } else {
        return { ...state, ordering: { columnName: action.columnName, order: 'asc' } };
      }
    } else {
      return { ...state, ordering: { columnName: action.columnName, order: 'desc' } };
    }
  case 'toggle-filters-visibility':
    return { ...state, filtersCollapsed: !state.filtersCollapsed };
  case 'toggle-race-filtering':
    return { ...state, raceFilter: { ...state.raceFilter, [action.race]: !state.raceFilter[action.race] } };
  case 'toggle-category-filtering':
    return { ...state, categoryFilter: { ...state.categoryFilter, [action.category]: !state.categoryFilter[action.category] } };
  case 'toggle-license-filtering':
    return { ...state, licenseFilter: { ...state.licenseFilter, [action.license]: !state.licenseFilter[action.license] } };
  default:
    return state;
  }
};

export default endlessShips;
