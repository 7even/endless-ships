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
  default:
    return state;
  }
};

export default endlessShips;
