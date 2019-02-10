import React from 'react';

const licenseLabelStyles = {
  'City-Ship':          'human',
  'Navy':               'human',
  'Carrier':            'human',
  'Cruiser':            'human',
  'Militia Carrier':    'human',
  'Unfettered Militia': 'hai',
  'Wanderer':           'wanderer',
  'Wanderer Military':  'wanderer',
  'Wanderer Outfits':   'wanderer',
  'Coalition':          'coalition',
  'Heliarch':           'coalition',
  'Remnant':            'remnant',
  'Remnant Capital':    'remnant'
};

const LicenseLabel = ({ license }) => {
  const style = licenseLabelStyles[license];
  const labelClass = `label label-${style}`;

  return <span className={labelClass}>{license}</span>;
};

export default LicenseLabel;
