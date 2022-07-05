function Product(props) {
  const { name, provider, image_url, description, price} = props;
  return (
    <div className="d-flex Product">

      <div className="flex-shrink-0">
        <img src={image_url} alt={name} />
      </div>

      <div class="flex-grow-1 ms-3">
        <div><strong>{name}</strong></div>
        <div><strong>Provider:</strong> <span>{provider}</span></div>
        <div className="my-4"><h3>{price} INR</h3></div>
        
        <div><strong>Description</strong></div>
        <div>{description}</div>
      </div>

    </div>
  );
}

export default Product;
