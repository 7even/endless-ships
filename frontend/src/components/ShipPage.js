import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { Row, Col, Panel, Image, Nav, NavItem, ListGroupItem } from 'react-bootstrap';
import R from 'ramda';
import { FormattedNumber, kebabCase, OutfitLink, renderAttribute, intersperse } from '../common';
import './ShipPage.css';

const ShipLicenses = ({ licenses }) => {
  if (licenses.length === 2) {
    return <p className="italic">This ship requires {licenses[0]} and {licenses[1]} licenses.</p>;
  } else {
    return <p className="italic">This ship requires a {licenses[0]} license.</p>;
  }
};

const imageURL = (ship) => {
  var filename;

  if (ship.name === "Shuttle") {
    // for some crazy reason Shuttle has different sprite filenames
    filename = "ship/shuttle=0.png";
  } else if (ship.sprite[1]) {
    filename = window.encodeURI(ship.sprite[0]) + "-0.png";
  } else {
    filename = window.encodeURI(ship.sprite[0]) + ".png";
  }

  // probably not a good idea to hotlink to github to the master branch
  return "https://raw.githubusercontent.com/endless-sky/endless-sky/master/images/" + filename;
};

const FormattedPercentage = ({ coefficient }) => (
  <span><FormattedNumber number={coefficient * 100}/>%</span>
);

const ShipDescription = ({ description }) => (
  <Row>
    <Col md={12}>
      <div className="well">
        {intersperse(description, (index) => <span key={index}><br/><br/></span>)}
      </div>
    </Col>
  </Row>
);

const modificationLink = (name, path) => (
  <NavItem componentClass={Link}
           key={name}
           eventKey={name}
           href={path}
           to={path}>
    {name}
  </NavItem>
);

const ShipModifications = ({ ship, modificationNames }) => {
  const items = modificationNames.map(modificationName => {
    return modificationLink(
      modificationName,
      `/ships/${kebabCase(ship.name)}/${kebabCase(modificationName)}`
    );
  });

  return (
    <Panel>
      <Panel.Heading>Modifications</Panel.Heading>

      <Panel.Body>
        <Nav bsStyle="pills" stacked={true} activeKey={ship.modification || ship.name}>
          {modificationLink(ship.name, `/ships/${kebabCase(ship.name)}`)}
          {items}
        </Nav>
      </Panel.Body>
    </Panel>
  );
};

const OutfitItem = ({ name, quantity }) => {
  if (quantity === 1) {
    return <li className="list-group-item"><OutfitLink outfitName={name} /></li>;
  } else {
    return (
      <li className="list-group-item">
        <span className="badge">{quantity}</span>
        <OutfitLink outfitName={name} />
      </li>
    );
  }
};

const OutfitsList = ({ outfits }) => {
  const outfitCategories = [
    'Guns',
    'Turrets',
    'Secondary Weapons',
    'Ammunition',
    'Systems',
    'Power',
    'Engines',
    'Hand to Hand',
    'Special'
  ];

  const items = outfitCategories.map(category => {
    if (R.has(category, outfits)) {
      const categoryHeader = <ListGroupItem key={category} disabled>{category}</ListGroupItem>;
      const categoryItems = R.pipe(
        R.prop(category),
        R.sortBy(R.path(['outfit', 'name']))
      )(outfits).map(({ outfit, quantity }) => (
        <OutfitItem key={outfit.name}
                    name={outfit.name}
                    quantity={quantity} />
      ));

      return [categoryHeader, ...categoryItems];
    } else {
      return [];
    }
  });

  return (
    <Panel.Body>
      <ul className="list-group">{items}</ul>
    </Panel.Body>
  );
};

const ShipPage = ({ ship, modificationNames }) => (
  <div className="app">
    <Row>
      <Col md={6}>
        <Panel>
          <Panel.Heading>{ship.name}</Panel.Heading>

          <Panel.Body>
            <div className="media">
              <div className="media-body">
                <ul>
                  {renderAttribute(ship, R.prop('cost'),    'cost')}
                  {renderAttribute(ship, R.prop('shields'), 'shields')}
                  {renderAttribute(ship, R.prop('hull'),    'hull')}
                  {renderAttribute(ship, R.prop('mass'),    'mass')}
                  {renderAttribute(ship, R.prop('cargoSpace'), 'cargo space')}
                  {renderAttribute(ship, R.prop('requiredCrew'), 'required crew')}
                  {renderAttribute(ship, R.prop('bunks'), 'bunks')}
                  {renderAttribute(ship, R.prop('fuelCapacity'), 'fuel capacity')}
                  {renderAttribute(ship, R.prop('outfitSpace'), 'outfit space')}
                  {renderAttribute(ship, R.prop('weaponCapacity'), 'weapon capacity')}
                  {renderAttribute(ship, R.prop('engineCapacity'), 'engine capacity')}
                  {renderAttribute(ship, R.propOr(0, 'guns'), 'guns')}
                  {renderAttribute(ship, R.propOr(0, 'turrets'), 'turrets')}
                  {ship.drones > 0 && renderAttribute(ship, R.prop('drones'), 'drones')}
                  {ship.fighters > 0 && renderAttribute(ship, R.prop('fighters'), 'fighters')}
                  {renderAttribute(ship, R.prop('ramscoop'), 'ramscoop')}
                  {renderAttribute(ship, R.prop('cloak'), 'cloak')}
                  {R.has('selfDestruct', ship) && <li>self-destruct: <FormattedPercentage coefficient={ship.selfDestruct}/></li>}
                </ul>

                {ship.licenses && <ShipLicenses licenses={ship.licenses} />}
              </div>

              <div className="media-right">
                <Image src={imageURL(ship)} className="ship-sprite" />
              </div>
            </div>
          </Panel.Body>
        </Panel>

        {modificationNames.length > 0 && <ShipModifications ship={ship} modificationNames={modificationNames} />}
      </Col>

      <Col md={6}>
        <Panel>
          <Panel.Heading>Default outfits</Panel.Heading>

          <OutfitsList outfits={ship.outfits} />
        </Panel>
      </Col>
    </Row>

    {ship.description && ship.description.length > 0 && <ShipDescription description={ship.description}/>}
  </div>
);

const mapStateToProps = (state, { match: { params: { shipName, modificationName } } }) => {
  const ship = state.ships.find(ship => kebabCase(ship.name) === shipName);
  const modifications = state.shipModifications.filter(modification => modification.name === ship.name);
  const selectedModification = modifications.find(modification => kebabCase(modification.modification) === modificationName);
  const shipWithModification = { ...ship, ...selectedModification };

  const outfits = shipWithModification.outfits.map(({ name, quantity }) => {
    const outfit = state.outfits.find(outfit => outfit.name === name);
    return { outfit, quantity };
  });

  return {
    ship: {
      ...shipWithModification,
      outfits: R.groupBy(R.path(['outfit', 'category']), outfits)
    },
    modificationNames: modifications.map(mod => mod.modification)
  };
};

export default connect(mapStateToProps)(ShipPage);
