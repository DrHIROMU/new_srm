const VendorInfoPage = () => {
  return (
    <>
      <h1>Vendor Info</h1>
      <form>
        <div className="query-condition">
          <div className="field-row">
            <div className="input-field">
              <label>Vendor Name/Code</label>
              <input type="text" />
            </div>
            <div className="input-field">
              <label>Group Key</label>
              <input type="text" />
            </div>
            <div className="input-field">
              <label>Vendor Type</label>
              <select>
                <option>1</option>
              </select>
            </div>
          </div>

          <div className="field-row">
            <div className="input-field">
              <label>Org</label>
              <select>
                <option>1</option>
              </select>
            </div>
            <div className="input-field">
              <label>Sourcer</label>
              <select>
                <option>1</option>
              </select>
            </div>
            <div className="input-field">
              <label>Part Category</label>
              <select>
                <option>1</option>
              </select>
            </div>
            <div className="input-field">
              <label>Block Status</label>
              <select>
                <option>1</option>
              </select>
            </div>
          </div>

          <div className="field-row">
            <div className="input-field">
              <label>PVL</label>
              <input type="text" />
            </div>
            <div className="input-field">
              <label>Vendor Level</label>
              <input type="text" />
            </div>
            <div className="input-field">
              <label>Strategy Class</label>
              <input type="text" />
            </div>
            <div className="input-field">
              <label>PLM Status</label>
              <select>
                <option>1</option>
              </select>
            </div>
          </div>
        </div>

        <div className="button-row"></div>
      </form>
    </>
  );
};

export default VendorInfoPage;
